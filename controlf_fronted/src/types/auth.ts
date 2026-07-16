/**
 * Datos del usuario autenticado, tal como los devuelve la API tras el login/registro.
 */
export interface AuthUser {
  id: number;
  email: string;
  nombre: string;
  rol: string;
}
