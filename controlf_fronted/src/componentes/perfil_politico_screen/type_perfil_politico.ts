/** Entrada del historial de coherencia: un voto real frente a lo esperado por una promesa vinculada. */
export type IHistorialCoherencia = {
  leyTitulo: string;
  votoReal: string;
  resultado: string;
  analisis: string;
};

/** Comentario publicado en el debate ciudadano de un político o una ley. */
export type ComentarioDebate = {
  id: string;
  usuario: string;
  mensaje: string;
  fecha: string;
  avatarUrl: string;
  puntaje?: number | null;
};

/** Entrada del historial persistente de cambios de patrimonio/antecedentes de un político. */
export type HistorialCambioPerfil = {
  campo: string;
  valorAnterior: string | null;
  valorNuevo: string | null;
  fecha: string;
};

/** Perfil completo de un político tal como lo expone `GET /api/politicos/:id`. */
export type PerfilPolitico = {
  id: string;
  nombre: string;
  organizacion: string;
  cargo: string;
  patrimonio: string;
  fotoUrl: string;
  antecedentes: string;
  estaActivo: boolean;
  porcentajeCoherencia: number;
  estadoEtiqueta: string;
  indiceReputacion: number;
  totalCalificaciones: number;
  etiquetaReputacion: string;
  historial: IHistorialCoherencia[];
  historialCambios: HistorialCambioPerfil[];
  comentarios: ComentarioDebate[];
};
