import React from 'react';

interface AuditoriaLeyProps {
  filas: {
    id: string;
    nombre: string;
    bloque: string;
    voto: string;
    analisisCoherencia: string;
    nivelCoherencia: string;
  }[];
}

const AuditoriaLey: React.FC<AuditoriaLeyProps> = ({ filas }) => {
  const getStatusColor = (nivel: string) => {
    switch (nivel.toLowerCase()) {
      case 'cumple':
      case 'coherente':
        return 'text-success-green bg-success-green/10';
      case 'incumple':
      case 'incoherente':
        return 'text-danger-red bg-danger-red/10';
      default:
        return 'text-warning-amber bg-warning-amber/10';
    }
  };

  return (
    <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden mb-8">
      <div className="px-8 py-6 border-b border-slate-100 bg-slate-50/50">
        <h4 className="text-sm font-bold text-primary-navy uppercase tracking-wide flex items-center gap-2">
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>
          Auditoría de Coherencia (Cruce de Datos)
        </h4>
      </div>

      <div className="overflow-x-auto">
        <table className="w-full text-left border-collapse">
          <thead>
            <tr className="bg-slate-50 text-[10px] font-black text-slate-400 uppercase tracking-widest">
              <th className="px-8 py-4 border-b border-slate-100">Político</th>
              <th className="px-6 py-4 border-b border-slate-100">Bloque</th>
              <th className="px-6 py-4 border-b border-slate-100 text-center">Voto</th>
              <th className="px-8 py-4 border-b border-slate-100">Análisis de Coherencia</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-50">
            {filas.length === 0 ? (
              <tr>
                <td colSpan={4} className="px-8 py-12 text-center text-slate-400 text-sm">
                  No hay datos de auditoría vinculados a esta ley.
                </td>
              </tr>
            ) : (
              filas.map((fila) => (
                <tr key={fila.id} className="hover:bg-slate-50/50 transition-colors">
                  <td className="px-8 py-5">
                    <div className="text-sm font-bold text-slate-700">{fila.nombre}</div>
                  </td>
                  <td className="px-6 py-5">
                    <div className="text-xs text-slate-500 font-medium">{fila.bloque}</div>
                  </td>
                  <td className="px-6 py-5 text-center">
                    <span className="px-3 py-1 rounded text-[10px] font-black bg-slate-100 text-slate-600 border border-slate-200">
                      {fila.voto}
                    </span>
                  </td>
                  <td className="px-8 py-5">
                    <div className="flex items-start gap-3">
                      <span className={`flex-shrink-0 px-2 py-0.5 rounded text-[10px] font-black uppercase border border-current ${getStatusColor(fila.nivelCoherencia)}`}>
                        {fila.nivelCoherencia === 'cumple' ? '✓' : '✗'} {fila.nivelCoherencia}
                      </span>
                      <p className="text-xs text-slate-500 leading-relaxed italic">
                        "{fila.analisisCoherencia}"
                      </p>
                    </div>
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

export default AuditoriaLey;
