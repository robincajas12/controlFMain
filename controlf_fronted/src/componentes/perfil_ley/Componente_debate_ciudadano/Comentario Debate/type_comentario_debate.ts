/**
 * Comentario individual dentro del debate ciudadano de una ley.
 */
export interface ComentarioDebate {
  id: string;            // Identificador único del comentario
  usuario: string;       // Nombre del autor
  fecha: string;         // Fecha de publicación, formateada
  mensaje: string;       // Texto del comentario
  avatarUrl: string;     // URL del avatar del autor
}