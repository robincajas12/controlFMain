/**
 * Endpoint:
 * - GET /api/leyes -> Obtiene listado general de leyes.
 */
interface ExpedienteLegislativo {
  id: string;
  tipoExpediente: string;
  codigoExpediente: string;
  titulo: string;
  estaAprobado: boolean;
  categoria: string;
  proponente: string;
  tieneMenuOpciones: boolean;
}