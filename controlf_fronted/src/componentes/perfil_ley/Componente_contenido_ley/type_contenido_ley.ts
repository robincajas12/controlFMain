/**
 * Endpoint:
 * - GET /api/leyes/{id} -> Obtiene resumen ejecutivo e impacto social.
 */
interface ContenidoLey {
  id: string;
  titulo: string;
  resumenEjecutivo: string;
  impactoSocial: string;
}