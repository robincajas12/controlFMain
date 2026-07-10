import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const Header = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { isAuthenticated, user, logout } = useAuth();

  const navItems = [
    { name: 'Dashboard', path: '/dashboard' },
    { name: 'Políticos', path: '/' },
    { name: 'Leyes', path: '/leyes' },
    { name: 'Admin', path: '/admin' },
  ];

  return (
    <header className="bg-primary-navy text-white shadow-md">
      <div className="container mx-auto px-4 h-16 flex items-center justify-between">
        <div className="flex items-center gap-2">
          <div className="bg-accent-blue p-1.5 rounded">
            <span className="font-bold text-lg leading-none">CF</span>
          </div>
          <h1 className="text-lg font-bold tracking-tight hidden sm:block">
            Plataforma de Auditoría Ciudadana
          </h1>
        </div>

        <nav className="flex items-center gap-1 sm:gap-4">
          {navItems.map((item) => {
            const isActive = location.pathname === item.path || (item.path !== '/' && location.pathname.startsWith(item.path));
            return (
              <Link
                key={item.path}
                to={item.path}
                className={`px-3 py-2 rounded-md text-sm font-medium transition-colors ${
                  isActive
                    ? 'bg-accent-blue text-white'
                    : 'text-slate-300 hover:text-white hover:bg-slate-700'
                }`}
              >
                {item.name}
              </Link>
            );
          })}
        </nav>

        <div className="flex items-center gap-3">
          {isAuthenticated ? (
            <>
              <div className="rounded-full bg-slate-700 px-3 py-1 text-xs font-semibold text-slate-100">{user?.nombre ?? user?.email}</div>
              <button onClick={() => { logout(); navigate('/login'); }} className="rounded-full border border-slate-600 px-3 py-1 text-sm text-slate-100">Salir</button>
            </>
          ) : (
            <Link to="/login" className="rounded-full bg-accent-blue px-3 py-1 text-sm font-semibold text-white">Entrar</Link>
          )}
        </div>
      </div>
    </header>
  );
};

export default Header;
