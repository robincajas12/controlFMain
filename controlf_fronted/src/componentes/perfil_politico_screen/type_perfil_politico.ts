export interface HistorialCoherencia {
  leyTitulo: string;
  votoReal: string;
  resultado: string;
  analisis: string;
}

export interface ComentarioDebate {
  id: string;
  usuario: string;
  mensaje: string;
  fecha: string;
  avatarUrl: string;
}

export interface PerfilPolitico {
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
  historial: HistorialCoherencia[];
  comentarios: ComentarioDebate[];
}
