import React from 'react';
import CartaPolitico from '../Componente carta politico/CartaPolitico';

interface CartaPoliticoData {
  id: string;
  nombre: string;
  organizacion: string;
  fotoUrl: string;
  estaActivo: boolean;
  estadoEtiqueta: string;
  porcentajeCoherencia: number;
  cantidadProyectos: number;
}

interface GrillaPoliticosProps {
  cartas: CartaPoliticoData[];
  isLoading?: boolean;
}

const GrillaPoliticos: React.FC<GrillaPoliticosProps> = ({ cartas = [], isLoading }) => {
  if (isLoading) {
    return (
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 animate-pulse">
        {[1, 2, 3, 4, 5, 6].map((i) => (
          <div key={i} className="bg-white rounded-xl h-96 border border-slate-200"></div>
        ))}
      </div>
    );
  }

  if (!cartas || cartas.length === 0) {
    return (
      <div className="bg-white rounded-xl border border-slate-200 p-12 text-center">
        <div className="text-slate-300 mb-4 flex justify-center">
          <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1" strokeLinecap="round" strokeLinejoin="round"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
        </div>
        <h3 className="text-xl font-bold text-primary-navy mb-2">No se encontraron políticos</h3>
        <p className="text-slate-500">Intenta ajustar los filtros o los términos de búsqueda.</p>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
      {cartas.map((politico) => (
        <CartaPolitico key={politico.id} politico={politico} />
      ))}
    </div>
  );
};

export default GrillaPoliticos;
