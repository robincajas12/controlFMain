import React from 'react';

interface FiltroLey {
  id: string;
  label: string;
  valorSeleccionado: string | null;
  opciones: string[];
}

interface BarraBusquedaLeyesProps {
  termino: string;
  filtros: FiltroLey[];
  onSearchChange: (value: string) => void;
  onFilterChange: (id: string, value: string) => void;
  onSearchSubmit: () => void;
}

/**
 * Cabecera y controles de búsqueda del directorio de leyes: término libre,
 * filtros dinámicos (categoría, estado) y botón de búsqueda. El estado de
 * los filtros y del término vive en el componente padre; este solo emite
 * los cambios.
 */
const BarraBusquedaLeyes: React.FC<BarraBusquedaLeyesProps> = ({
  termino,
  filtros,
  onSearchChange,
  onFilterChange,
  onSearchSubmit
}) => {
  return (
    <div className="mb-8">
      <div className="mb-6">
        <h2 className="text-2xl font-bold text-primary-navy">Directorio de Leyes y Propuestas</h2>
        <p className="text-slate-500">Consulta el estado y avance de la legislación nacional</p>
      </div>

      <div className="bg-white p-4 rounded-xl shadow-sm border border-slate-200 space-y-4">
        <div className="flex flex-col lg:flex-row gap-3">
          <div className="relative flex-grow">
            <input
              type="text"
              value={termino}
              onChange={(e) => onSearchChange(e.target.value)}
              placeholder="Ingrese palabra clave o número de ley..."
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

export default BarraBusquedaLeyes;
