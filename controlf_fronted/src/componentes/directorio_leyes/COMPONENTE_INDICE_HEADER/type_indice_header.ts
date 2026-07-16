/**
 * Encabezado con las secciones de navegación interna del directorio de leyes.
 */
export interface IndiceHeader {
  id: string;
  secciones: {
    nombre: string;      // Nombre de la sección
    icono: string;       // Ícono asociado
    estaActivo: boolean; // Si es la sección actualmente visible
  }[];
}