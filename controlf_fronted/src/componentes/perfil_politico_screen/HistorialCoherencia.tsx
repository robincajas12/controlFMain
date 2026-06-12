import React from 'react';
import { HistorialCoherencia as HistorialType } from './type_perfil_politico';

interface HistorialCoherenciaProps {
  historial: HistorialType[];
}

const HistorialCoherencia: React.FC<HistorialCoherenciaProps> = ({ historial }) => {
  const getStatusStyle = (resultado: string) => {
    switch (resultado.toUpperCase()) {
      case 'CUMPLE':
      case 'COHERENTE':
        return 'text-success-green bg-success-green/5';
      case 'INCUMPLE':
      case 'INCOHERENTE':
        return 'text-danger-red bg-danger-red/5';
      default:
        return 'text-warning-amber bg-warning-amber/5';
    }
  };

  const getStatusIcon = (resultado: string) => {
    switch (resultado.toUpperCase()) {
      case 'CUMPLE':
      case 'COHERENTE':
        return (
          <div className="w-5 h-5 rounded-full bg-success-green flex items-center justify-center text-white">
            <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
          </div>
        );
      case 'INCUMPLE':
      case 'INCOHERENTE':
        return (
          <div className="w-5 h-5 rounded-full bg-danger-red flex items-center justify-center text-white">
            <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
          </div>
        );
      default:
        return (
          <div className="w-5 h-5 rounded-full bg-warning-amber flex items-center justify-center text-white">
            <span className="font-bold text-[10px]">?</span>
          </div>
        );
    }
  };

  return (
    <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden mb-8">
      <div className="px-8 py-6 border-b border-slate-100 bg-slate-50/50">
        <h4 className="text-sm font-bold text-primary-navy uppercase tracking-wide flex items-center gap-2">
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M12 20V10"/><path d="M18 20V4"/><path d="M6 20v-4"/></svg>
          Historial de Coherencia (Promesas vs Votos)
        </h4>
      </div>

      <div className="overflow-x-auto">
        <table className="w-full text-left border-collapse">
          <thead>
            <tr className="bg-slate-50 text-[10px] font-black text-slate-400 uppercase tracking-widest">
              <th className="px-8 py-4 border-b border-slate-100">Propuesta de Ley / Promesa</th>
              <th className="px-4 py-4 border-b border-slate-100 text-center">Voto Real</th>
              <th className="px-4 py-4 border-b border-slate-100 text-center">Resultado</th>
              <th className="px-8 py-4 border-b border-slate-100">Análisis de Auditoría</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-50">
            {historial.length === 0 ? (
              <tr>
                <td colSpan={4} className="px-8 py-12 text-center text-slate-400 text-sm">
                  No hay registros de coherencia para este político.
                </td>
              </tr>
            ) : (
              historial.map((item, index) => (
                <tr key={index} className="hover:bg-slate-50/50 transition-colors group">
                  <td className="px-8 py-5">
                    <div className="text-sm font-bold text-slate-700 group-hover:text-primary-navy transition-colors">
                      {item.leyTitulo}
                    </div>
                  </td>
                  <td className="px-4 py-5 text-center">
                    <span className={`inline-block px-3 py-1 rounded text-[10px] font-black border ${
                      item.votoReal === 'FAVOR' ? 'text-blue-600 bg-blue-50 border-blue-100' : 
                      item.votoReal === 'CONTRA' ? 'text-red-600 bg-red-50 border-red-100' :
                      'text-slate-500 bg-slate-100 border-slate-200'
                    }`}>
                      {item.votoReal}
                    </span>
                  </td>
                  <td className="px-4 py-5">
                    <div className="flex justify-center items-center gap-2">
                      {getStatusIcon(item.resultado)}
                      <span className={`text-[10px] font-black uppercase tracking-tight ${getStatusStyle(item.resultado).split(' ')[0]}`}>
                        {item.resultado}
                      </span>
                    </div>
                  </td>
                  <td className="px-8 py-5">
                    <p className="text-xs text-slate-500 leading-relaxed italic">
                      "{item.analisis}"
                    </p>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default HistorialCoherencia;
