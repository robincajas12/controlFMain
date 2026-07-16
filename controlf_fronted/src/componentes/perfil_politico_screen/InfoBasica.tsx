import React from 'react';

interface InfoBasicaProps {
  nombre: string;
  organizacion: string;
  cargo: string;
  patrimonio: string;
  fotoUrl: string;
  estaActivo: boolean;
  antecedentes: string;
}

/**
 * Encabezado del perfil de un político: foto, datos generales y resumen
 * curricular (antecedentes).
 */
const InfoBasica: React.FC<InfoBasicaProps> = ({
  nombre,
  organizacion,
  cargo,
  patrimonio,
  fotoUrl,
  estaActivo,
  antecedentes
}) => {
  const antecedentesRef = React.useRef<HTMLDivElement>(null);

  const handleVerAntecedentes = () => {
    antecedentesRef.current?.scrollIntoView({ behavior: 'smooth', block: 'start' });
  };

  return (
    <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-8 mb-8">
      <div className="flex flex-col md:flex-row gap-8 items-start">
        <div className="relative">
          <div className="w-40 h-40 rounded-2xl bg-slate-100 border-4 border-white shadow-sm overflow-hidden">
            {fotoUrl ? (
              <img src={fotoUrl} alt={nombre} className="w-full h-full object-cover" />
            ) : (
              <div className="w-full h-full flex items-center justify-center text-slate-300">
                <svg xmlns="http://www.w3.org/2000/svg" width="80" height="80" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1" strokeLinecap="round" strokeLinejoin="round"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
              </div>
            )}
          </div>
          {estaActivo && (
            <div className="absolute -bottom-2 -right-2 bg-success-green text-white px-3 py-1 rounded-full text-xs font-bold border-4 border-white shadow-sm">
              ACTIVO
            </div>
          )}
        </div>

        <div className="flex-grow space-y-4">
          <div>
            <div className="text-slate-500 text-sm font-medium mb-1 uppercase tracking-wider">Perfil del Político</div>
            <h2 className="text-3xl font-bold text-primary-navy leading-tight">{nombre}</h2>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div className="flex items-center gap-3">
              <div className="p-2 bg-slate-50 rounded-lg text-slate-400">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M22 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
              </div>
              <div>
                <div className="text-xs text-slate-400 font-medium uppercase">Partido</div>
                <div className="text-sm font-bold text-slate-700">{organizacion}</div>
              </div>
            </div>

            <div className="flex items-center gap-3">
              <div className="p-2 bg-slate-50 rounded-lg text-slate-400">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="2" y="7" width="20" height="14" rx="2" ry="2"/><path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"/></svg>
              </div>
              <div>
                <div className="text-xs text-slate-400 font-medium uppercase">Cargo</div>
                <div className="text-sm font-bold text-slate-700">{cargo}</div>
              </div>
            </div>

            <div className="flex items-center gap-3">
              <div className="p-2 bg-slate-50 rounded-lg text-slate-400">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>
              </div>
              <div>
                <div className="text-xs text-slate-400 font-medium uppercase">Patrimonio</div>
                <div className="text-sm font-bold text-slate-700">{patrimonio}</div>
              </div>
            </div>
          </div>

          <div className="pt-4">
            <button
              type="button"
              onClick={handleVerAntecedentes}
              disabled={!antecedentes}
              className="flex items-center gap-2 px-6 py-2 bg-primary-navy text-white rounded-lg text-sm font-bold hover:bg-slate-800 transition-colors shadow-sm disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>
              Ver Antecedentes Completos
            </button>
          </div>
        </div>
      </div>

      {antecedentes && (
        <div ref={antecedentesRef} className="mt-8 pt-8 border-t border-slate-100">
          <h4 className="text-sm font-bold text-primary-navy uppercase mb-3 tracking-wide">Resumen Curricular</h4>
          <p className="text-slate-600 leading-relaxed text-sm">
            {antecedentes}
          </p>
        </div>
      )}
    </div>
  );
};

export default InfoBasica;
