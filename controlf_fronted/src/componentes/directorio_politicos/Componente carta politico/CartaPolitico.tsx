import React from 'react';
import { Link } from 'react-router-dom';

interface CartaPoliticoProps {
  politico: {
    id: string;
    nombre: string;
    organizacion: string;
    fotoUrl: string;
    estaActivo: boolean;
    estadoEtiqueta: string;
    porcentajeCoherencia: number;
    cantidadProyectos: number;
  };
}

const CartaPolitico: React.FC<CartaPoliticoProps> = ({ politico }) => {
  const getBadgeColor = (estado: string) => {
    switch (estado.toUpperCase()) {
      case 'COHERENTE':
        return 'bg-success-green/10 text-success-green border-success-green/20';
      case 'INCOHERENTE':
        return 'bg-danger-red/10 text-danger-red border-danger-red/20';
      case 'AMBIGUO':
      case 'ABSTENCIÓN ALTA':
        return 'bg-warning-amber/10 text-warning-amber border-warning-amber/20';
      default:
        return 'bg-slate-100 text-slate-600 border-slate-200';
    }
  };

  return (
    <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-6 flex flex-col items-center text-center transition-all hover:shadow-md">
      <div className="relative mb-4">
        <div className="w-24 h-24 rounded-full bg-slate-100 border-2 border-slate-200 overflow-hidden">
          {politico.fotoUrl ? (
            <img src={politico.fotoUrl} alt={politico.nombre} className="w-full h-full object-cover" />
          ) : (
            <div className="w-full h-full flex items-center justify-center text-slate-400">
              <svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
            </div>
          )}
        </div>
        {politico.estaActivo && (
          <div className="absolute bottom-1 right-1 w-5 h-5 bg-success-green border-4 border-white rounded-full shadow-sm" title="Activo"></div>
        )}
      </div>

      <h3 className="font-bold text-lg text-primary-navy mb-1">{politico.nombre}</h3>
      <p className="text-slate-500 text-sm mb-3">{politico.organizacion}</p>

      <div className={`px-3 py-1 rounded-full text-[10px] font-bold border uppercase tracking-wider mb-4 ${getBadgeColor(politico.estadoEtiqueta)}`}>
        {politico.estadoEtiqueta}
      </div>

      <div className="w-full space-y-3 mb-6">
        <div className="flex justify-between items-end">
          <span className="text-xs text-slate-400 font-medium">Coherencia</span>
          <span className="text-sm font-bold text-primary-navy">{politico.porcentajeCoherencia}%</span>
        </div>
        <div className="w-full bg-slate-100 h-2 rounded-full overflow-hidden">
          <div 
            className={`h-full rounded-full transition-all duration-1000 ${
              politico.porcentajeCoherencia >= 70 ? 'bg-success-green' : politico.porcentajeCoherencia >= 40 ? 'bg-warning-amber' : 'bg-danger-red'
            }`}
            style={{ width: `${politico.porcentajeCoherencia}%` }}
          ></div>
        </div>
        <div className="text-xs text-slate-400">
          <span className="font-semibold text-slate-600">{politico.cantidadProyectos}</span> Proyectos presentados
        </div>
      </div>

      <Link 
        to={`/politico/${politico.id}`}
        className="w-full py-2.5 bg-accent-blue text-white rounded-lg font-bold text-sm transition-colors hover:bg-blue-600 focus:ring-4 focus:ring-blue-100"
      >
        Ver Perfil Completo
      </Link>
    </div>
  );
};

export default CartaPolitico;
