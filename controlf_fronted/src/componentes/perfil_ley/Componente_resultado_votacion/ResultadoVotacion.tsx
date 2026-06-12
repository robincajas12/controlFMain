import React from 'react';

interface ResultadoVotacionProps {
  favor: number;
  contra: number;
  abstencion: number;
  total: number;
}

const ResultadoVotacion: React.FC<ResultadoVotacionProps> = ({ favor, contra, abstencion, total }) => {
  const getPercentage = (val: number) => (total > 0 ? (val / total) * 100 : 0);

  return (
    <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden mb-8">
      <div className="px-8 py-6 border-b border-slate-100 bg-slate-50/50 flex items-center justify-between">
        <h4 className="text-sm font-bold text-primary-navy uppercase tracking-wide flex items-center gap-2">
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M12 20V10"/><path d="M18 20V4"/><path d="M6 20v-4"/></svg>
          Resultado de la Votación
        </h4>
        <div className="text-[10px] font-black text-slate-400 uppercase tracking-widest">Total Votos: {total}</div>
      </div>

      <div className="p-8 space-y-6">
        {/* Favor */}
        <div className="space-y-2">
          <div className="flex justify-between items-end">
            <span className="text-xs font-bold text-success-green uppercase">Votos Favorables</span>
            <span className="text-sm font-black text-primary-navy">{favor}</span>
          </div>
          <div className="w-full h-3 bg-slate-100 rounded-full overflow-hidden border border-slate-200 shadow-inner">
            <div 
              className="h-full bg-success-green shadow-sm transition-all duration-1000"
              style={{ width: `${getPercentage(favor)}%` }}
            ></div>
          </div>
        </div>

        {/* Contra */}
        <div className="space-y-2">
          <div className="flex justify-between items-end">
            <span className="text-xs font-bold text-danger-red uppercase">Votos en Contra</span>
            <span className="text-sm font-black text-primary-navy">{contra}</span>
          </div>
          <div className="w-full h-3 bg-slate-100 rounded-full overflow-hidden border border-slate-200 shadow-inner">
            <div 
              className="h-full bg-danger-red shadow-sm transition-all duration-1000"
              style={{ width: `${getPercentage(contra)}%` }}
            ></div>
          </div>
        </div>

        {/* Abstención */}
        <div className="space-y-2">
          <div className="flex justify-between items-end">
            <span className="text-xs font-bold text-warning-amber uppercase">Abstenciones</span>
            <span className="text-sm font-black text-primary-navy">{abstencion}</span>
          </div>
          <div className="w-full h-3 bg-slate-100 rounded-full overflow-hidden border border-slate-200 shadow-inner">
            <div 
              className="h-full bg-warning-amber shadow-sm transition-all duration-1000"
              style={{ width: `${getPercentage(abstencion)}%` }}
            ></div>
          </div>
        </div>

        <div className="pt-4 border-t border-slate-50">
          <button className="text-[10px] font-black text-accent-blue hover:underline uppercase tracking-widest">
            Ver Lista Completa de Votantes por Bloque
          </button>
        </div>
      </div>
    </div>
  );
};

export default ResultadoVotacion;
