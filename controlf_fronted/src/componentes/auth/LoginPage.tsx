import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useLocation, Navigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { isValidEmail } from './authValidation';

interface FieldErrors {
  email?: string;
  password?: string;
}

const inputBaseClass =
  'w-full rounded-xl border bg-slate-50/50 px-4 py-3 text-sm text-slate-800 shadow-sm transition-colors placeholder:text-slate-400 focus:bg-white focus:outline-none focus:ring-2';

const FRASES = [
  'La herramienta del pueblo y para el pueblo',
  'Transparencia que rinde cuentas',
  'Fiscaliza el poder, defiende lo público',
  'Cada peso, bajo la lupa',
  'La rendición de cuentas al alcance de todos',
  'Datos que exigen responsabilidad',
  'Vigilancia ciudadana, gestión clara',
  'Auditar es un derecho, no un privilegio',
  'Del dato a la acción, sin intermediarios',
  'El control ciudadano empieza aquí',
  'Poder observado, poder responsable',
];

/**
 * Página de inicio de sesión. Valida el formulario en el cliente antes de
 * enviarlo y redirige a la ruta de origen (`location.state.from`) tras un
 * login exitoso.
 */
const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { login, isAuthenticated } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [fieldErrors, setFieldErrors] = useState<FieldErrors>({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  const [fraseIndex, setFraseIndex] = useState(0);
  const [fraseVisible, setFraseVisible] = useState(true);

  const from = (location.state as { from?: string } | null)?.from ?? '/';

  useEffect(() => {
    const CICLO = 4500;
    const DURACION_FADE = 600;

    const interval = setInterval(() => {
      setFraseVisible(false);
      window.setTimeout(() => {
        setFraseIndex((prev) => (prev + 1) % FRASES.length);
        setFraseVisible(true);
      }, DURACION_FADE);
    }, CICLO);

    return () => clearInterval(interval);
  }, []);

  if (isAuthenticated) {
    return <Navigate to={from} replace />;
  }

  const validar = (): FieldErrors => {
    const errores: FieldErrors = {};
    if (!email.trim()) {
      errores.email = 'Ingresa tu correo electrónico.';
    } else if (!isValidEmail(email)) {
      errores.email = 'El correo no tiene un formato válido.';
    }
    if (!password) {
      errores.password = 'Ingresa tu contraseña.';
    }
    return errores;
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    setError('');

    const errores = validar();
    setFieldErrors(errores);
    if (Object.keys(errores).length > 0) {
      return;
    }

    setIsSubmitting(true);
    try {
      const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
      });
      if (!response.ok) {
        setError('Credenciales inválidas');
        return;
      }
      const data = await response.json();
      login(data.token, {
        id: data.user?.id ?? 0,
        email: data.user?.email ?? email,
        nombre: data.user?.nombre ?? email,
        rol: data.user?.rol ?? 'CIUDADANO',
      });
      navigate(from, { replace: true });
    } catch {
      setError('No se pudo conectar con el servidor');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="login-animated-bg min-h-screen w-full flex flex-col items-center justify-center px-4 py-12">
      <header className="mb-10 text-center">
        <span className="inline-flex items-center gap-2 rounded-full border border-white/20 bg-white/10 px-4 py-1.5 text-[11px] font-bold uppercase tracking-[0.25em] text-white/80 shadow-sm backdrop-blur-sm">
          <span className="h-1.5 w-1.5 rounded-full bg-violet-300"></span>
          Plataforma de auditoría ciudadana
        </span>
        <h1 className="mt-6 text-5xl sm:text-6xl font-black tracking-tighter text-white drop-shadow-sm">
          Control<span className="text-violet-300">&nbsp;F</span>
        </h1>
        <div className="mx-auto mt-4 h-px w-24 bg-gradient-to-r from-transparent via-white/40 to-transparent"></div>
      </header>

      <div className="w-full max-w-md rounded-2xl border border-slate-200 bg-white p-8 shadow-xl shadow-slate-200/60">
        <h2 className="text-2xl font-black tracking-tight text-primary-navy">Iniciar sesión</h2>
        <p className="mt-2 text-sm text-slate-500">
          Accede con tu cuenta para comentar, calificar y administrar el sistema.
        </p>

        <form onSubmit={handleSubmit} className="mt-7 space-y-4" noValidate>
          {error && (
            <p className="rounded-xl border border-rose-200 bg-rose-50 p-3 text-sm font-medium text-rose-600">
              {error}
            </p>
          )}

          <div className="space-y-1.5">
            <label htmlFor="email" className="block text-xs font-bold uppercase tracking-wide text-slate-500">
              Correo electrónico
            </label>
            <input
              id="email"
              value={email}
              onChange={(e) => {
                setEmail(e.target.value);
                setFieldErrors((prev) => ({ ...prev, email: undefined }));
              }}
              type="email"
              autoComplete="email"
              placeholder="correo@dominio.com"
              className={`${inputBaseClass} ${fieldErrors.email ? 'border-rose-300 focus:border-rose-400 focus:ring-rose-200' : 'border-slate-200 focus:border-accent-blue focus:ring-accent-blue/20'}`}
            />
            {fieldErrors.email && (
              <p className="text-xs font-medium text-rose-600">{fieldErrors.email}</p>
            )}
          </div>

          <div className="space-y-1.5">
            <label htmlFor="password" className="block text-xs font-bold uppercase tracking-wide text-slate-500">
              Contraseña
            </label>
            <input
              id="password"
              value={password}
              onChange={(e) => {
                setPassword(e.target.value);
                setFieldErrors((prev) => ({ ...prev, password: undefined }));
              }}
              type="password"
              autoComplete="current-password"
              placeholder="••••••••"
              className={`${inputBaseClass} ${fieldErrors.password ? 'border-rose-300 focus:border-rose-400 focus:ring-rose-200' : 'border-slate-200 focus:border-accent-blue focus:ring-accent-blue/20'}`}
            />
            {fieldErrors.password && (
              <p className="text-xs font-medium text-rose-600">{fieldErrors.password}</p>
            )}
          </div>

          <button
            type="submit"
            disabled={isSubmitting}
            className="w-full rounded-xl bg-primary-navy px-4 py-3 text-sm font-bold text-white shadow-sm transition-all hover:bg-slate-800 focus:outline-none focus:ring-2 focus:ring-primary-navy/30 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {isSubmitting ? 'Ingresando...' : 'Entrar'}
          </button>
        </form>

        <p className="mt-6 text-center text-sm text-slate-500">
          ¿No tienes cuenta?{' '}
          <Link to="/registro" className="font-semibold text-accent-blue hover:text-blue-600">
            Registrarse
          </Link>
        </p>
      </div>

      <div className="mt-10 flex h-12 items-center justify-center px-4">
        <p
          className={`max-w-lg text-center text-base sm:text-lg font-medium italic text-white/85 drop-shadow-sm transition-opacity duration-[600ms] ease-in-out ${
            fraseVisible ? 'opacity-100' : 'opacity-0'
          }`}
        >
          “{FRASES[fraseIndex]}”
        </p>
      </div>
    </div>
  );
};

export default LoginPage;
