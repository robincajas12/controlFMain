import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

interface ProtectedRouteProps {
  children: React.ReactNode;
  /** Si es `true`, exige rol `ADMIN` exactamente (atajo equivalente a `roles={['ADMIN']}`). */
  adminOnly?: boolean;
  /** Lista de roles permitidos; si se omite, solo se exige estar autenticado. */
  roles?: string[];
}

/**
 * Envuelve una ruta para exigir autenticación y, opcionalmente, un rol
 * específico. Redirige a `/login` (recordando la ruta de origen) si no
 * hay sesión, o muestra un mensaje de acceso denegado si el rol no alcanza.
 */
const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children, adminOnly = false, roles }) => {
  const { isAuthenticated, role, isLoading } = useAuth();
  const location = useLocation();

  if (isLoading) {
    return null;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />;
  }

  if (adminOnly && role !== 'ADMIN') {
    return <div className="rounded-2xl border border-amber-200 bg-amber-50 p-8 text-center text-amber-700">No tienes permisos para ver esta página.</div>;
  }

  if (roles && (!role || !roles.includes(role))) {
    return <div className="rounded-2xl border border-amber-200 bg-amber-50 p-8 text-center text-amber-700">No tienes permisos para ver esta página.</div>;
  }

  return <>{children}</>;
};

export default ProtectedRoute;
