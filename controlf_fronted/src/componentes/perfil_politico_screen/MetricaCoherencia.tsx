import React from 'react';

interface MetricaCoherenciaProps {
  porcentaje: number;
  estadoEtiqueta: string;
}

/**
 * Indicador visual del porcentaje de coherencia entre promesas de
 * campaña y votos de un político.
 */
const MetricaCoherencia: React.FC<MetricaCoherenciaProps> = ({ porcentaje, estadoEtiqueta }) => {
  const getColorClasses = (val: number) => {
    if (val >= 70) return 'text-success-green bg-success-green/10 border-success-green/20 fill-success-green';
    if (val >= 40) return 'text-warning-amber bg-warning-amber/10 border-warning-amber/20 fill-warning-amber';
    return 'text-danger-red bg-danger-red/10 border-danger-red/20 fill-danger-red';
  };

  const getProgressColor = (val: number) => {
    if (val >= 70) return 'bg-success-green';
    if (val >= 40) return 'bg-warning-amber';
    return 'bg-danger-red';
  };

  return (
    <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-8 mb-8">
      <h4 className="text-sm font-bold text-primary-navy uppercase mb-6 tracking-wide flex items-center gap-2">
        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M20.24 12.24a6 6 0 0 0-8.49-8.49L5 10.5V19h8.5z"/><line x1="16" y1="8" x2="2" y2="22"/><line x1="17.5" y1="15" x2="9" y2="15"/></svg>
        Métrica de Coherencia (RF-04/05)
      </h4>

      <div className="flex flex-col md:flex-row items-center gap-12">
        <div className={`relative flex flex-col items-center justify-center w-32 h-32 rounded-full border-4 ${getColorClasses(porcentaje)}`}>
          <span className="text-3xl font-black">{porcentaje}%</span>
          <span className="text-[10px] font-bold uppercase tracking-tighter opacity-70">COHERENTE</span>
        </div>

        <div className="flex-grow w-full space-y-6">
          <div className="space-y-2">
            <div className="flex justify-between items-end">
              <span className="text-sm font-bold text-slate-700">Índice de Alineación (Promesas vs Votos)</span>
              <span className={`text-xs font-black px-2 py-0.5 rounded border uppercase ${getColorClasses(porcentaje)}`}>
                {estadoEtiqueta}
              </span>
            </div>
            <div className="w-full h-4 bg-slate-100 rounded-full overflow-hidden border border-slate-200 shadow-inner">
              <div 
                className={`h-full rounded-full transition-all duration-1000 ${getProgressColor(porcentaje)} shadow-sm`}
                style={{ width: `${porcentaje}%` }}
              ></div>
            </div>
            <div className="flex justify-between text-[10px] font-bold text-slate-400 uppercase tracking-widest pt-1">
              <span>Crítico (0%)</span>
              <span>Moderado (50%)</span>
              <span>Excelente (100%)</span>
            </div>
          </div>

          <p className="text-xs text-slate-500 italic">
            * Este índice se calcula promediando la coherencia técnica de cada voto emitido frente a las promesas de campaña vinculadas oficialmente.
          </p>
        </div>
      </div>
    </div>
  );
};

export default MetricaCoherencia;
