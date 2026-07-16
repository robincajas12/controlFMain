package com.controlf.service;

import com.controlf.db.repository.LeyRepository;
import com.controlf.db.repository.PoliticoRepository;
import com.controlf.db.repository.VotoRepository;
import com.controlf.db.schema.Ley;
import com.controlf.db.schema.Politico;
import com.controlf.db.schema.Voto;
import com.controlf.db.schema.enums.EstadoLey;
import com.controlf.db.schema.enums.TipoVoto;
import com.controlf.dto.AssemblyMemberDTO;
import com.controlf.dto.ImportResultDTO;
import com.controlf.dto.VotingDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Importa miembros de la Asamblea Nacional del Ecuador y el detalle de sus
 * votaciones desde el portal de datos abiertos, y los traduce al modelo
 * interno de {@code Politico}, {@code Ley} y {@code Voto}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AssemblyImportService {

    private static final List<String> IRRELEVANT = List.of(
            "himno", "instalación", "quorum", "receso",
            "lectura", "acta", "homenaje", "minuto de silencio"
    );

    private static final String BASE_URL = "https://datos.asambleanacional.gob.ec/ecurul/assemblyman";

    private final LeyRepository leyRepository;
    private final VotoRepository votoRepository;
    private final PoliticoRepository politicoRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Obtiene los miembros de la asamblea desde la fuente externa,
     * deduplicados por identificador (la fuente devuelve una entrada por
     * cada período legislativo en el que participó el mismo asambleísta).
     *
     * @return los miembros de la asamblea, uno por identificador
     */
    public List<AssemblyMemberDTO> getAssemblyMembers() {
        String url = "https://datos.asambleanacional.gob.ec/ecurul/assemblyman/assembly?idType=1&onlyActive=false&idPeriod=&idTerritorial=";
        ResponseEntity<List<AssemblyMemberDTO>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {}
        );
        List<AssemblyMemberDTO> members = response.getBody();
        if (members == null) {
            return List.of();
        }

        Map<Long, AssemblyMemberDTO> uniquePorId = new LinkedHashMap<>();
        for (AssemblyMemberDTO member : members) {
            uniquePorId.putIfAbsent(member.getId(), member);
        }
        return new ArrayList<>(uniquePorId.values());
    }

    /**
     * Consulta las votaciones de un miembro de la asamblea directamente
     * contra el portal externo (fuera de {@link RestTemplate}, ya que este
     * endpoint responde HTML en lugar de JSON cuando no hay resultados).
     *
     * @param memberId identificador externo del miembro de la asamblea
     * @return las votaciones encontradas, o una lista vacía si la fuente
     *         no devolvió resultados (respuesta HTML en vez de JSON)
     * @throws Exception si la petición HTTP o el parseo de la respuesta fallan
     */
    public List<VotingDTO> getVotings(Long memberId) throws Exception {
        String url = String.format(
                BASE_URL + "/findVotings?periodId=&assemblyMemberId=%d&dateIn=&dateOut=&sessionNumber=&theme=&proposal=&offset=0&limit=",
                memberId
        );

        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36")
                .header("Accept-Language", "en-US,en;q=0.8")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

if (response.body().trim().startsWith("<")) {
    log.info("Sin votaciones para memberId={}", memberId);
    return List.of();
}

log.debug("findVotings status={} bodyPreview={}", response.statusCode(),
        response.body().substring(0, Math.min(100, response.body().length())));

return objectMapper.readValue(response.body(),
        objectMapper.getTypeFactory().constructCollectionType(List.class, VotingDTO.class));
    }

    /**
     * Importa únicamente las votaciones seleccionadas de un miembro de la
     * asamblea (sin filtrar por relevancia), vinculándolas al político
     * local con el mismo nombre completo.
     *
     * @param assemblyMemberId identificador externo del miembro de la asamblea
     * @param selectedIds identificadores externos de las votaciones a importar
     * @return el resultado de la importación (encontradas, importadas, ignoradas, duplicadas)
     * @throws RuntimeException si falla la consulta a la fuente externa, si
     *         el miembro no existe, o si no hay un político local con ese nombre
     */
    public ImportResultDTO importSelectedVotings(Long assemblyMemberId, List<Long> selectedIds) {
        log.info("Iniciando importación seleccionada para assemblyMemberId={}", assemblyMemberId);

        List<VotingDTO> votings;
        try {
            votings = getVotings(assemblyMemberId);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener votaciones: " + e.getMessage(), e);
        }

        int found = selectedIds == null ? 0 : selectedIds.size();
        int imported = 0;
        int ignored = 0;
        int duplicates = 0;

        AssemblyMemberDTO member = getAssemblyMembers().stream()
                .filter(m -> assemblyMemberId.equals(m.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Asambleísta no encontrado: " + assemblyMemberId));

        String nombre = (member.getFirstName() + " " + member.getLastname()).trim().toUpperCase();
        Politico politico = politicoRepository.findByNombreCompletoContainingIgnoreCase(nombre)
                .orElseThrow(() -> new RuntimeException("No se encontró político local para: " + nombre));

        if (votings != null) {
            DateTimeFormatter fmt = new DateTimeFormatterBuilder()
                    .appendPattern("yyyy-MM-dd HH:mm:ss")
                    .appendFraction(java.time.temporal.ChronoField.NANO_OF_SECOND, 0, 9, true)
                    .toFormatter();
            for (VotingDTO v : votings) {
                if (selectedIds != null && !selectedIds.contains(v.getId())) {
                    continue;
                }
                LocalDateTime ldt = LocalDateTime.parse(v.getVotingDate(), fmt);
                if (leyRepository.existsByExternalId(v.getId())) {
                    Ley existingLaw = leyRepository.findByExternalId(v.getId()).orElse(null);
                    if (existingLaw != null) {
                        if (votoRepository.existsByPoliticoIdAndLeyId(politico.getId(), existingLaw.getId())) {
                            duplicates++;
                            log.debug("Voto duplicado omitido politico={} external_id={}", politico.getNombreCompleto(), v.getId());
                            continue;
                        }

                        Voto voto = new Voto();
                        voto.setPolitico(politico);
                        voto.setLey(existingLaw);
                        voto.setTipoVoto(mapVote(v.getDescription()));
                        voto.setAsistencia(true);
                        voto.setFechaVoto(ldt);
                        votoRepository.save(voto);

                        imported++;
                        log.debug("Voto agregado a ley existente external_id={} politico={}", v.getId(), politico.getNombreCompleto());
                        continue;
                    }
                    duplicates++;
                    log.debug("Duplicado omitido external_id={}", v.getId());
                    continue;
                }

                Ley ley = new Ley();
                ley.setTitulo(truncate(v.getProposalDescription(), 255));
                ley.setCodigo("AN-" + v.getId());
                ley.setTipoExpediente("VOTACION_ASAMBLEA");
                ley.setProponente(nombre);
                ley.setDescripcionOriginal(v.getThemeDescription());
                ley.setCategoria(classifyLaw(ley.getTitulo(), ley.getDescripcionOriginal()));
                ley.setEstado(EstadoLey.DEBATE);
                ley.setFechaIngreso(ldt.toLocalDate());
                ley.setExternalId(v.getId());
                ley = leyRepository.save(ley);

                Voto voto = new Voto();
                voto.setPolitico(politico);
                voto.setLey(ley);
                voto.setTipoVoto(mapVote(v.getDescription()));
                voto.setAsistencia(true);
                voto.setFechaVoto(ldt);
                votoRepository.save(voto);

                imported++;
            }
        }

        log.info("Importación seleccionada finalizada: encontradas={} importadas={} ignoradas={} duplicadas={}",
                found, imported, ignored, duplicates);
        return new ImportResultDTO(found, imported, ignored, duplicates);
    }

    /**
     * Importa las votaciones de cada político indicado, resolviendo su
     * miembro de asamblea equivalente por nombre completo. Los políticos
     * sin equivalente en la fuente externa se cuentan como ignorados.
     *
     * @param politicoIds identificadores de los políticos locales a sincronizar
     * @return el resultado agregado de la importación
     * @throws RuntimeException si {@code politicoIds} es nulo o está vacío
     */
    public ImportResultDTO importLeyesForPoliticos(List<Integer> politicoIds) {
        log.info("Iniciando importación por candidatos: {}", politicoIds);
        if (politicoIds == null || politicoIds.isEmpty()) {
            throw new RuntimeException("Debe seleccionar al menos un político");
        }

        int imported = 0;
        int duplicates = 0;
        int ignored = 0;

        Map<Integer, Politico> politicos = politicoRepository.findAllById(politicoIds).stream()
                .collect(Collectors.toMap(Politico::getId, politico -> politico));

        for (Integer politicoId : politicoIds) {
            Politico politico = politicos.get(politicoId);
            if (politico == null) {
                continue;
            }

            List<AssemblyMemberDTO> members = getAssemblyMembers();
            AssemblyMemberDTO member = members.stream()
                    .filter(m -> politico.getNombreCompleto() != null && (m.getFirstName() + " " + m.getLastname()).trim().equalsIgnoreCase(politico.getNombreCompleto().trim()))
                    .findFirst()
                    .orElse(null);

            if (member == null) {
                ignored++;
                continue;
            }

            ImportResultDTO result = importVotings(member.getId());
            imported += result.getImported();
            duplicates += result.getDuplicates();
            ignored += result.getIgnored();
        }

        return new ImportResultDTO(politicoIds.size(), imported, ignored, duplicates);
    }

    /**
     * Importa todas las votaciones relevantes (ver {@link #isRelevant})
     * de un miembro de la asamblea. Las votaciones cuyo identificador
     * externo ya existe se vinculan a la ley existente en vez de crear una
     * nueva, y se omiten como duplicadas si el político ya tiene un voto
     * registrado para esa ley.
     *
     * @param assemblyMemberId identificador externo del miembro de la asamblea
     * @return el resultado de la importación (encontradas, importadas, ignoradas, duplicadas)
     * @throws RuntimeException si falla la consulta a la fuente externa, si
     *         el miembro no existe, o si no hay un político local con ese nombre
     */
    public ImportResultDTO importVotings(Long assemblyMemberId) {
        log.info("Iniciando importación para assemblyMemberId={}", assemblyMemberId);

        List<VotingDTO> votings;
        try {
            votings = getVotings(assemblyMemberId);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener votaciones: " + e.getMessage(), e);
        }

        int found = votings == null ? 0 : votings.size();
        int imported = 0;
        int ignored = 0;
        int duplicates = 0;

        AssemblyMemberDTO member = getAssemblyMembers().stream()
                .filter(m -> assemblyMemberId.equals(m.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Asambleísta no encontrado: " + assemblyMemberId));

        String nombre = (member.getFirstName() + " " + member.getLastname()).trim().toUpperCase();
        Politico politico = politicoRepository.findByNombreCompletoContainingIgnoreCase(nombre)
                .orElseThrow(() -> new RuntimeException("No se encontró político local para: " + nombre));

        if (votings != null) {
           DateTimeFormatter fmt = new DateTimeFormatterBuilder()
    .appendPattern("yyyy-MM-dd HH:mm:ss")
    .appendFraction(java.time.temporal.ChronoField.NANO_OF_SECOND, 0, 9, true)
    .toFormatter();
            for (VotingDTO v : votings) {
                if (!isRelevant(v)) {
                    ignored++;
                    log.debug("Votación ignorada (irrelevante): {}", v.getProposalDescription());
                    continue;
                }
                LocalDateTime ldt = LocalDateTime.parse(v.getVotingDate(), fmt);
                if (leyRepository.existsByExternalId(v.getId())) {
                    Ley existingLaw = leyRepository.findByExternalId(v.getId()).orElse(null);
                    if (existingLaw != null) {
                        if (votoRepository.existsByPoliticoIdAndLeyId(politico.getId(), existingLaw.getId())) {
                            duplicates++;
                            log.debug("Voto duplicado omitido politico={} external_id={}", politico.getNombreCompleto(), v.getId());
                            continue;
                        }

                        Voto voto = new Voto();
                        voto.setPolitico(politico);
                        voto.setLey(existingLaw);
                        voto.setTipoVoto(mapVote(v.getDescription()));
                        voto.setAsistencia(true);
                        voto.setFechaVoto(ldt);
                        votoRepository.save(voto);

                        imported++;
                        log.debug("Voto agregado a ley existente external_id={} politico={}", v.getId(), politico.getNombreCompleto());
                        continue;
                    }
                    duplicates++;
                    log.debug("Duplicado omitido external_id={}", v.getId());
                    continue;
                }

                Ley ley = new Ley();
                ley.setTitulo(truncate(v.getProposalDescription(), 255));
                ley.setCodigo("AN-" + v.getId());
                ley.setTipoExpediente("VOTACION_ASAMBLEA");
                ley.setProponente(nombre);
                ley.setDescripcionOriginal(v.getThemeDescription());
                ley.setCategoria(classifyLaw(ley.getTitulo(), ley.getDescripcionOriginal()));
                ley.setEstado(EstadoLey.DEBATE);
                ley.setFechaIngreso(ldt.toLocalDate());
                ley.setExternalId(v.getId());
                ley = leyRepository.save(ley);

                Voto voto = new Voto();
                voto.setPolitico(politico);
                voto.setLey(ley);
                voto.setTipoVoto(mapVote(v.getDescription()));
                voto.setAsistencia(true);
                voto.setFechaVoto(ldt);
                votoRepository.save(voto);

                imported++;
            }
        }

        log.info("Importación finalizada: encontradas={} importadas={} ignoradas={} duplicadas={}",
                found, imported, ignored, duplicates);
        return new ImportResultDTO(found, imported, ignored, duplicates);
    }

    /**
     * Clasifica una ley en una categoría temática a partir de palabras
     * clave presentes en su título o descripción.
     *
     * @param titulo título de la ley
     * @param descripcion descripción de la ley
     * @return la categoría detectada, o {@code "GENERAL"} si no coincide con ninguna palabra clave
     */
    private String classifyLaw(String titulo, String descripcion) {
        String combined = ((titulo == null ? "" : titulo) + " " + (descripcion == null ? "" : descripcion)).toLowerCase();
        if (combined.contains("educ") || combined.contains("escuela") || combined.contains("universidad")) {
            return "EDUCACION";
        }
        if (combined.contains("salud") || combined.contains("hospital") || combined.contains("medic")) {
            return "SALUD";
        }
        if (combined.contains("trabajo") || combined.contains("empleo") || combined.contains("labor")) {
            return "TRABAJO";
        }
        if (combined.contains("seguridad") || combined.contains("polic") || combined.contains("delito")) {
            return "SEGURIDAD";
        }
        return "GENERAL";
    }

    /**
     * Traduce la descripción de voto de la fuente externa al enum interno.
     *
     * @param description descripción de voto recibida de la fuente externa (p. ej. {@code "SI"}, {@code "NO"})
     * @return el {@link TipoVoto} correspondiente, o {@code ABSTENCION} si es nula o desconocida
     */
    private TipoVoto mapVote(String description) {
        if (description == null) {
            log.warn("Voto nulo recibido, asignando ABSTENCION");
            return TipoVoto.ABSTENCION;
        }
        return switch (description.trim().toUpperCase()) {
            case "SI" -> TipoVoto.FAVOR;
            case "NO" -> TipoVoto.CONTRA;
            case "ABSTENCION" -> TipoVoto.ABSTENCION;
            default -> {
                log.warn("Voto desconocido: '{}', asignando ABSTENCION", description);
                yield TipoVoto.ABSTENCION;
            }
        };
    }

    /**
     * Filtra votaciones puramente procedimentales (instalación de sesión,
     * lectura de actas, homenajes, etc.) que no representan una decisión
     * legislativa sustantiva.
     *
     * @param v votación a evaluar
     * @return {@code true} si no contiene ninguna palabra clave de {@link #IRRELEVANT}
     */
    private boolean isRelevant(VotingDTO v) {
        String combined = ((v.getProposalDescription() == null ? "" : v.getProposalDescription()) + " " +
                (v.getThemeDescription() == null ? "" : v.getThemeDescription())).toLowerCase();
        return IRRELEVANT.stream().noneMatch(combined::contains);
    }

    /**
     * @param value texto a truncar; puede ser {@code null}
     * @param maxLength longitud máxima permitida
     * @return el texto truncado a {@code maxLength} caracteres, o {@code null} si el valor era {@code null}
     */
    private String truncate(String value, int maxLength) {
        if (value == null) return null;
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }
}
