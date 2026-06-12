import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import ContenidoLey from './Componente_contenido_ley/ContenidoLey';
import ResultadoVotacion from './Componente_resultado_votacion/ResultadoVotacion';
import AuditoriaLey from './Componente_auditoria_coherencia/AuditoriaLey';
import ParticipacionCiudadana from '../perfil_politico_screen/ParticipacionCiudadana';

const PerfilLeyPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [perfil, setPerfil] = useState<any>(null);
  const [isLoading, setIsLoading] = useState(true);

  const fetchPerfil = async () => {
    setIsLoading(true);
    try {
      const response = await fetch(`http://localhost:8080/api/leyes/${id}/perfil`);
      const data = await response.json();
      setPerfil(data);
    } catch (error) {
      console.error("Error al cargar perfil de ley:", error);
      navigate('/leyes');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchPerfil();
  }, [id]);

  const handleAddComentario = async (texto: string, puntaje: number) => {
    try {
      await fetch(`http://localhost:8080/api/leyes/${id}/comentarios`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ texto, usuarioId: 1 })
      });
      await fetch(`http://localhost:8080/api/leyes/${id}/calificaciones`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ puntaje, usuarioId: 1 })
      });
      fetchPerfil();
    } catch (error) {
      console.error("Error al publicar comentario:", error);
    }
  };

  if (isLoading) {
    return (
      <div className="max-w-5xl mx-auto animate-pulse space-y-8">
        <div className="bg-white h-48 rounded-xl border border-slate-200"></div>
        <div className="bg-white h-64 rounded-xl border border-slate-200"></div>
        <div className="bg-white h-96 rounded-xl border border-slate-200"></div>
      </div>
    );
  }

  if (!perfil) return null;

  return (
    <div className="max-w-5xl mx-auto">
      <button 
        onClick={() => navigate('/leyes')}
        className="flex items-center gap-2 text-slate-500 hover:text-primary-navy font-bold text-sm mb-6 transition-colors group"
      >
        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" className="group-hover:-translate-x-1 transition-transform"><line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/></svg>
        Volver al Directorio de Leyes
      </button>

      <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-8 mb-8 border-l-8 border-l-accent-blue">
        <div className="text-slate-500 text-[10px] font-black uppercase tracking-widest mb-2">Expediente Legislativo</div>
        <h2 className="text-2xl font-black text-primary-navy mb-4 leading-tight">
          {perfil.contenido.titulo}
        </h2>
        <div className="flex flex-wrap gap-4 text-xs font-bold text-slate-500">
          <span className="bg-slate-100 px-3 py-1 rounded">ID: {perfil.contenido.id}</span>
          <span className="bg-success-green/10 text-success-green px-3 py-1 rounded border border-success-green/20">APROBADA</span>
        </div>
      </div>

      <ContenidoLey 
        id={perfil.contenido.id}
        titulo={perfil.contenido.titulo}
        resumenEjecutivo={perfil.contenido.resumenEjecutivo}
        impactoSocial={perfil.contenido.impactoSocial}
      />

      <ResultadoVotacion 
        favor={perfil.votacion.votosFavor}
        contra={perfil.votacion.votosContra}
        abstencion={perfil.votacion.votosAbstencion}
        total={perfil.votacion.votosFavor + perfil.votacion.votosContra + perfil.votacion.votosAbstencion}
      />

      <AuditoriaLey 
        filas={perfil.auditoria.filas}
      />

      <ParticipacionCiudadana 
        comentarios={perfil.debate.comentarios}
        onAddComentario={handleAddComentario}
      />
    </div>
  );
};

export default PerfilLeyPage;
