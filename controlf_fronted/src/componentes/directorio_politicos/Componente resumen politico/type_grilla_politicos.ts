/**
 * Endpoint: GET /api/politicos
 * Query Params: ?pagina={number}&size={number}
 * 
 * Response Body:
 * {
 *   "id": string,
 *   "cartas": CartaPolitico[],
 *   "paginaActual": number,
 *   "totalPaginas": number
 * }
 */
interface GrillaPoliticos {
  id: string;                  // Identificador de la grilla
  cartas: CartaPolitico[];     // Lista de tarjetas (ver type_carta_politico.ts)
  paginaActual: number;        // Página actual
  totalPaginas: number;        // Total de páginas
}