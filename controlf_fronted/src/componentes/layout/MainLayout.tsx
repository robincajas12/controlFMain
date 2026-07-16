import { Outlet } from 'react-router-dom';
import Header from './Header';

/**
 * Layout base de la aplicación: encabezado fijo, contenido de la ruta
 * activa (`Outlet`) y pie de página.
 */
const MainLayout = () => {
  return (
    <div className="min-h-screen flex flex-col bg-bg-dashboard">
      <Header />
      <main className="flex-grow container mx-auto px-4 py-8">
        <Outlet />
      </main>
      <footer className="bg-white border-t border-slate-200 py-6 mt-auto">
        <div className="container mx-auto px-4 text-center text-slate-500 text-sm">
          &copy; {new Date().getFullYear()} ControlF - Plataforma de Auditoría Ciudadana. Todos los derechos reservados.
        </div>
      </footer>
    </div>
  );
};

export default MainLayout;
