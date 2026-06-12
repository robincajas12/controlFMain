/**
 * Endpoint: GET /api/admin/panel
 * 
 * Response Body:
 * {
 *   "tituloSeccion": string,
 *   "opciones": [
 *     {
 *       "nombreOpcion": string,
 *       "icono": string,
 *       "notificacionBadge": number
 *     }
 *   ]
 * }
 */
interface PanelControl {
  tituloSeccion: string;
  opciones: {
    nombreOpcion: string;
    icono: string;
    notificacionBadge?: number;
  }[];
}