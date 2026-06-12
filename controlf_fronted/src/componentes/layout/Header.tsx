import { Link, useLocation } from 'react-router-dom';

const Header = () => {
  const location = useLocation();

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
          <button className="text-slate-300 hover:text-white p-2 rounded-full transition-colors">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9"/><path d="M10.3 21a1.94 1.94 0 0 0 3.4 0"/></svg>
          </button>
          <div className="h-8 w-8 rounded-full bg-slate-500 flex items-center justify-center text-xs font-bold">
            JD
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;
