/**
 * Fila de la tabla de auditoría de coherencia: cruce entre el voto de un
 * político en una ley y sus promesas de campaña relacionadas.
 */
export interface FilaAuditoria {
  id: string;                    // Identificador único del político
  nombre: string;                // Nombre del político
  fotoUrl: string;               // URL del avatar
  bloque: string;                // Bloque o partido político
  voto: "FAVOR" | "CONTRA" | "ABSTENCIÓN"; // Tipo de voto
  analisisCoherencia: string;    // Texto del análisis
  nivelCoherencia: "cumple" | "incumple" | "ambiguo"; // Estado visual del ícono
}