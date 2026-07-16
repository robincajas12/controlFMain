import React from 'react';

interface PaginacionProps {
  paginaActual: number;
  totalPaginas: number;
  onPageChange: (page: number) => void;
}

/**
 * Controles de paginación (anterior/siguiente); no se renderiza si solo hay una página.
 */
const Paginacion: React.FC<PaginacionProps> = ({
  paginaActual,
  totalPaginas,
  onPageChange
}) => {
  if (totalPaginas <= 1) return null;

  const hayAnterior = paginaActual > 1;
  const haySiguiente = paginaActual < totalPaginas;

  return (
    <div className="mt-12 flex items-center justify-center gap-4">
      <button
        onClick={() => onPageChange(paginaActual - 1)}
        disabled={!hayAnterior}
        className={`flex items-center gap-1 px-4 py-2 rounded-lg text-sm font-bold transition-all ${
          hayAnterior
            ? 'bg-white border border-slate-200 text-primary-navy hover:bg-slate-50 shadow-sm'
            : 'bg-slate-50 text-slate-300 border border-slate-100 cursor-not-allowed'
        }`}
      >
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polyline points="15 18 9 12 15 6"/></svg>
        Anterior
      </button>

      <div className="flex items-center gap-2">
        <span className="text-sm text-slate-500 font-medium">Página</span>
        <span className="px-3 py-1 bg-accent-blue text-white rounded text-sm font-bold shadow-sm">
          {paginaActual}
        </span>
        <span className="text-sm text-slate-500 font-medium">de {totalPaginas}</span>
      </div>

      <button
        onClick={() => onPageChange(paginaActual + 1)}
        disabled={!haySiguiente}
        className={`flex items-center gap-1 px-4 py-2 rounded-lg text-sm font-bold transition-all ${
          haySiguiente
            ? 'bg-white border border-slate-200 text-primary-navy hover:bg-slate-50 shadow-sm'
            : 'bg-slate-50 text-slate-300 border border-slate-100 cursor-not-allowed'
        }`}
      >
        Siguiente
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polyline points="9 18 15 12 9 6"/></svg>
      </button>
    </div>
  );
};

export default Paginacion;
