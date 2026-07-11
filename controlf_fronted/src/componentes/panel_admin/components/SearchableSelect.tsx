import React, { useEffect, useMemo, useRef, useState } from 'react';

export interface SearchableSelectOption {
  id: string;
  label: string;
}

interface SearchableSelectProps {
  /** Opciones disponibles en el desplegable. */
  options: SearchableSelectOption[];
  /** Id de la opción seleccionada actualmente ('' cuando no hay selección). */
  value: string;
  /** Se dispara con el id de la opción elegida. */
  onChange: (id: string) => void;
  /** Texto guía dentro del campo de búsqueda. */
  placeholder?: string;
  /** Deshabilita por completo el selector. */
  disabled?: boolean;
  /** Cuando es true, además del nombre permite buscar por el id (útil para leyes). */
  searchById?: boolean;
  /** Texto que se muestra cuando no hay coincidencias. */
  emptyText?: string;
}

/**
 * Selector con búsqueda por texto reutilizable para el panel de administración.
 * Evita renderizar listas gigantes: la lista solo aparece cuando el usuario abre
 * el desplegable y se cierra automáticamente al hacer clic fuera del componente.
 */
const SearchableSelect: React.FC<SearchableSelectProps> = ({
  options,
  value,
  onChange,
  placeholder = 'Buscar...',
  disabled = false,
  searchById = false,
  emptyText = 'Sin resultados',
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const [search, setSearch] = useState('');
  const containerRef = useRef<HTMLDivElement>(null);

  const selectedOption = options.find((option) => option.id === value) ?? null;

  // Cierra el desplegable al hacer clic fuera para no dejarlo abierto ni renderizado.
  useEffect(() => {
    if (!isOpen) return;

    const handleClickOutside = (event: MouseEvent) => {
      if (containerRef.current && !containerRef.current.contains(event.target as Node)) {
        setIsOpen(false);
        setSearch('');
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [isOpen]);

  const filteredOptions = useMemo(() => {
    const term = search.trim().toLowerCase();
    if (!term) return options;
    return options.filter((option) => {
      const matchesLabel = option.label.toLowerCase().includes(term);
      const matchesId = searchById && option.id.toLowerCase().includes(term);
      return matchesLabel || matchesId;
    });
  }, [options, search, searchById]);

  const handleSelect = (option: SearchableSelectOption) => {
    onChange(option.id);
    setIsOpen(false);
    setSearch('');
  };

  return (
    <div ref={containerRef} className="relative">
      <button
        type="button"
        disabled={disabled}
        onClick={() => setIsOpen((open) => !open)}
        className="w-full flex items-center justify-between gap-2 px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm font-bold text-slate-700 focus:outline-none focus:ring-2 focus:ring-accent-blue/20 transition-all disabled:opacity-50 disabled:cursor-not-allowed"
      >
        <span className={selectedOption ? 'truncate text-left' : 'truncate text-left text-slate-400'}>
          {selectedOption ? selectedOption.label : placeholder}
        </span>
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" className={`shrink-0 text-slate-400 transition-transform ${isOpen ? 'rotate-180' : ''}`}><polyline points="6 9 12 15 18 9" /></svg>
      </button>

      {isOpen && (
        <div className="absolute z-20 mt-1 w-full rounded-xl border border-slate-200 bg-white shadow-lg">
          <div className="p-2 border-b border-slate-100">
            <input
              type="text"
              autoFocus
              value={search}
              onChange={(event) => setSearch(event.target.value)}
              placeholder={searchById ? 'Buscar por nombre o ID...' : 'Buscar por nombre...'}
              className="w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-700 focus:border-accent-blue focus:outline-none"
            />
          </div>
          <div className="max-h-64 overflow-y-auto">
            {filteredOptions.length > 0 ? (
              filteredOptions.map((option) => {
                const isSelected = option.id === value;
                return (
                  <div
                    key={option.id}
                    onClick={() => handleSelect(option)}
                    className={`px-4 py-3 text-sm cursor-pointer hover:bg-slate-50 border-b border-slate-100 last:border-0 ${isSelected ? 'bg-accent-blue/10 text-accent-blue font-bold' : 'text-slate-700'}`}
                  >
                    {searchById ? (
                      <span>
                        <span className="text-[10px] font-black text-slate-400 mr-2">#{option.id}</span>
                        {option.label}
                      </span>
                    ) : (
                      option.label
                    )}
                  </div>
                );
              })
            ) : (
              <div className="px-4 py-3 text-sm text-slate-400 italic">{emptyText}</div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default SearchableSelect;
