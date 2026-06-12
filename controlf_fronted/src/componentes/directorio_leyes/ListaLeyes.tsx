import React from 'react';
import { Link } from 'react-router-dom';

interface ExpedienteLegislativo {
  id: string;
  codigoExpediente: string;
  tituloLey: string;
  categoria: string;
  estado: string;
  estaAprobado: boolean;
  accionUrl: string;
}

interface ListaLeyesProps {
  leyes: ExpedienteLegislativo[];
  isLoading: boolean;
}

const ListaLeyes: React.FC<ListaLeyesProps> = ({ leyes, isLoading }) => {
  const getEstadoBadge = (estado: string) => {
    switch (estado.toUpperCase()) {
      case 'APROBADA':
        return 'bg-success-green/10 text-success-green border-success-green/20';
      case 'VETADA':
        return 'bg-danger-red/10 text-danger-red border-danger-red/20';
      case 'EN DEBATE':
        return 'bg-warning-amber/10 text-warning-amber border-warning-amber/20';
      default:
        return 'bg-slate-100 text-slate-500 border-slate-200';
    }
  };

  if (isLoading) {
    return (
      <div className="bg-white rounded-xl border border-slate-200 overflow-hidden animate-pulse">
        <div className="h-12 bg-slate-50 border-b border-slate-100"></div>
        {[1, 2, 3, 4, 5].map((i) => (
          <div key={i} className="h-16 border-b border-slate-50"></div>
        ))}
      </div>
    );
  }

  return (
    <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
      <div className="overflow-x-auto">
        <table className="w-full text-left border-collapse">
          <thead>
            <tr className="bg-slate-50 text-[10px] font-black text-slate-400 uppercase tracking-widest">
              <th className="px-6 py-4 border-b border-slate-100">Código</th>
              <th className="px-6 py-4 border-b border-slate-100">Título de la Ley</th>
              <th className="px-6 py-4 border-b border-slate-100">Categoría</th>
              <th className="px-6 py-4 border-b border-slate-100">Estado</th>
              <th className="px-6 py-4 border-b border-slate-100 text-center">Acción</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-50">
            {leyes.length === 0 ? (
              <tr>
                <td colSpan={5} className="px-6 py-12 text-center text-slate-400 text-sm">
                  No se encontraron leyes con los filtros aplicados.
                </td>
              </tr>
            ) : (
              leyes.map((ley) => (
                <tr key={ley.id} className="hover:bg-slate-50/50 transition-colors group">
                  <td className="px-6 py-4 text-xs font-bold text-slate-500">
                    {ley.codigoExpediente}
                  </td>
                  <td className="px-6 py-4">
                    <div className="text-sm font-bold text-primary-navy line-clamp-1 group-hover:text-accent-blue transition-colors">
                      {ley.tituloLey}
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <span className="text-xs text-slate-500 font-medium">{ley.categoria}</span>
                  </td>
                  <td className="px-6 py-4">
                    <span className={`px-2 py-0.5 rounded text-[10px] font-black border uppercase ${getEstadoBadge(ley.estado)}`}>
                      {ley.estado}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-center">
                    <Link
                      to={`/ley/${ley.id}`}
                      className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-slate-100 text-slate-600 rounded-lg text-[10px] font-black hover:bg-accent-blue hover:text-white transition-all"
                    >
                      VER EXPEDIENTE
                      <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round"><polyline points="9 18 15 12 9 6"/></svg>
                    </Link>
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

export default ListaLeyes;
