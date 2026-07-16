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

/**
 * Tarjeta resumen de un político en el directorio: foto, organización,
 * porcentaje de coherencia y enlace a su expediente completo.
 */
const CartaPolitico: React.FC<CartaPoliticoProps> = ({ politico }) => {
  /** Color de la etiqueta de estado según el nivel de coherencia del político. */
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
    <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-5 flex flex-col items-center text-center transition-all hover:shadow-lg hover:-translate-y-0.5 group">
      <div className="relative mb-3">
        <div className="w-20 h-20 rounded-xl bg-slate-50 border-2 border-white shadow-sm overflow-hidden group-hover:scale-105 transition-transform duration-300">
          {politico.fotoUrl ? (
            <img src={politico.fotoUrl} alt={politico.nombre} className="w-full h-full object-cover" />
          ) : (
            <div className="w-full h-full flex items-center justify-center text-slate-300">
              <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1" strokeLinecap="round" strokeLinejoin="round"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
            </div>
          )}
        </div>
        {politico.estaActivo && (
          <div className="absolute -bottom-0.5 -right-0.5 bg-success-green text-white px-2 py-0.5 rounded-md text-[8px] font-black border border-white shadow-sm uppercase tracking-tighter">
            Activo
          </div>
        )}
      </div>

      <div className="mb-2">
        <h3 className="font-black text-[15px] text-primary-navy leading-tight tracking-tighter line-clamp-1">{politico.nombre}</h3>
        <p className="text-slate-400 text-[10px] font-black uppercase tracking-widest mt-0.5">{politico.organizacion}</p>
      </div>

      <div className={`px-3 py-1 rounded-full text-[9px] font-black border uppercase tracking-widest mb-4 ${getBadgeColor(politico.estadoEtiqueta)}`}>
        {politico.estadoEtiqueta}
      </div>

      <div className="w-full space-y-2.5 mb-5 bg-slate-50/50 p-3 rounded-lg border border-slate-100">
        <div>
          <div className="flex justify-between items-end mb-1">
            <span className="text-[10px] text-slate-400 font-black uppercase tracking-wider">Coherencia</span>
            <span className="text-[13px] font-black text-primary-navy">{politico.porcentajeCoherencia}%</span>
          </div>
          <div className="w-full bg-slate-200 h-2 rounded-full overflow-hidden">
            <div 
              className={`h-full rounded-full transition-all duration-1000 ${
                politico.porcentajeCoherencia >= 70 ? 'bg-success-green' : politico.porcentajeCoherencia >= 40 ? 'bg-warning-amber' : 'bg-danger-red'
              }`}
              style={{ width: `${politico.porcentajeCoherencia}%` }}
            ></div>
          </div>
        </div>
        
        <div className="flex items-center justify-center gap-2 pt-1.5 border-t border-slate-200/60">
           <svg xmlns="http://www.w3.org/2000/svg" width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" className="text-slate-400"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>
           <span className="text-[10px] font-black text-slate-500 uppercase tracking-tight">
             <span className="text-primary-navy">{politico.cantidadProyectos}</span> Proyectos
           </span>
        </div>
      </div>

      <Link 
        to={`/politico/${politico.id}`}
        className="w-full py-3 bg-primary-navy text-white rounded-lg font-black text-[11px] transition-all hover:bg-slate-800 active:scale-95 uppercase tracking-widest text-center shadow-md shadow-slate-200"
      >
        Ver Expediente
      </Link>
    </div>
  );
};

export default CartaPolitico;
