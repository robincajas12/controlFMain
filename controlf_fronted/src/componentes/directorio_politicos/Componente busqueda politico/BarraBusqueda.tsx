import React from 'react';

interface FiltroBusqueda {
  id: string;
  label: string;
  valorSeleccionado: string | null;
  opciones: string[];
}

interface BarraBusquedaProps {
  titulo: string;
  subtitulo: string;
  filtros: FiltroBusqueda[];
  textoBusqueda: string;
  placeholderBusqueda: string;
  textoBotonExportar: string;
  onSearchChange: (value: string) => void;
  onFilterChange: (id: string, value: string) => void;
  onSearchSubmit: () => void;
  onExport: () => void;
}

const BarraBusqueda: React.FC<BarraBusquedaProps> = ({
  titulo,
  subtitulo,
  filtros,
  textoBusqueda,
  placeholderBusqueda,
  textoBotonExportar,
  onSearchChange,
  onFilterChange,
  onSearchSubmit,
  onExport
}) => {
  return (
    <div className="mb-8">
      <div className="flex flex-col md:flex-row md:items-end justify-between gap-4 mb-6">
        <div>
          <h2 className="text-2xl font-bold text-primary-navy">{titulo}</h2>
          <p className="text-slate-500">{subtitulo}</p>
        </div>
        <button
          onClick={onExport}
          className="flex items-center gap-2 px-4 py-2 bg-white border border-slate-200 rounded-lg text-sm font-medium text-slate-600 hover:bg-slate-50 transition-colors shadow-sm"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>
          {textoBotonExportar}
        </button>
      </div>

      <div className="bg-white p-4 rounded-xl shadow-sm border border-slate-200 space-y-4">
        <div className="flex flex-col lg:flex-row gap-3">
          <div className="relative flex-grow">
            <input
              type="text"
              value={textoBusqueda}
              onChange={(e) => onSearchChange(e.target.value)}
              placeholder={placeholderBusqueda}
              className="w-full pl-10 pr-4 py-2.5 bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-accent-blue/20 focus:border-accent-blue transition-all"
            />
            <div className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
            </div>
          </div>
          
          <div className="flex flex-wrap gap-3">
            {filtros.map((filtro) => (
              <select
                key={filtro.id}
                value={filtro.valorSeleccionado || ''}
                onChange={(e) => onFilterChange(filtro.id, e.target.value)}
                className="px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-lg text-sm font-medium text-slate-600 focus:outline-none focus:ring-2 focus:ring-accent-blue/20 focus:border-accent-blue transition-all cursor-pointer"
              >
                <option value="">{filtro.label}</option>
                {filtro.opciones.map((opcion) => (
                  <option key={opcion} value={opcion}>{opcion}</option>
                ))}
              </select>
            ))}
            
            <button 
              onClick={onSearchSubmit}
              className="px-6 py-2.5 bg-accent-blue text-white rounded-lg font-bold text-sm hover:bg-blue-600 transition-colors shadow-sm"
            >
              Buscar
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default BarraBusqueda;
