import React, { useState, useEffect } from 'react';

interface SimpleItem {
  id: string;
  label: string;
}

const MotorCoherencia: React.FC = () => {
  const [politicos, setPoliticos] = useState<SimpleItem[]>([]);
  const [leyes, setLeyes] = useState<SimpleItem[]>([]);
  const [promesas, setPromesas] = useState<SimpleItem[]>([]);
  
  const [selectedPolitico, setSelectedPolitico] = useState('');
  const [selectedPromesa, setSelectedPromesa] = useState('');
  const [selectedLey, setSelectedLey] = useState('');
  const [impacto, setImpacto] = useState('POSITIVO');
  const [analisis, setAnalisis] = useState('');

  useEffect(() => {
    fetch('http://localhost:8080/api/admin/motor/data')
      .then(res => res.json())
      .then(data => {
        setPoliticos(data.politicos);
        setLeyes(data.leyes);
      });
  }, []);

  useEffect(() => {
    if (selectedPolitico) {
      fetch(`http://localhost:8080/api/admin/politicos/${selectedPolitico}/promesas`)
        .then(res => res.json())
        .then(data => setPromesas(data));
    } else {
      setPromesas([]);
    }
  }, [selectedPolitico]);

  const handleGenerarVinculo = async () => {
    if (!selectedPromesa || !selectedLey) return;

    try {
      await fetch('http://localhost:8080/api/admin/vinculos', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          promesaId: parseInt(selectedPromesa),
          leyId: parseInt(selectedLey),
          impactoEsperado: impacto,
          nivelCoherencia: 'CUMPLE', // Simplificado
          analisis: analisis
        })
      });
      alert('Vínculo generado exitosamente');
      setAnalisis('');
    } catch (error) {
      console.error("Error al generar vínculo:", error);
    }
  };

  return (
    <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden mb-8">
      <div className="px-8 py-6 border-b border-slate-100 bg-slate-50/50">
        <h4 className="text-sm font-bold text-primary-navy uppercase tracking-wide flex items-center gap-2">
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="10"/><path d="M12 16v-4"/><path d="M12 8h.01"/></svg>
          Motor de Coherencia Técnica (RF-03 / RF-04)
        </h4>
        <p className="text-xs text-slate-400 mt-1 uppercase font-black tracking-tighter">Vinculación de promesas electorales con legislación real</p>
      </div>

      <div className="p-8 grid grid-cols-1 lg:grid-cols-2 gap-12">
        <div className="space-y-6">
          <div className="space-y-2">
            <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest">1. Seleccionar Político</label>
            <select 
              value={selectedPolitico}
              onChange={(e) => setSelectedPolitico(e.target.value)}
              className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm font-bold text-slate-700 focus:outline-none focus:ring-2 focus:ring-accent-blue/20 transition-all"
            >
              <option value="">-- Seleccionar Político --</option>
              {politicos.map(p => <option key={p.id} value={p.id}>{p.label}</option>)}
            </select>
          </div>

          <div className="space-y-2">
            <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest">2. Seleccionar Promesa</label>
            <select 
              disabled={!selectedPolitico}
              value={selectedPromesa}
              onChange={(e) => setSelectedPromesa(e.target.value)}
              className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm font-bold text-slate-700 focus:outline-none focus:ring-2 focus:ring-accent-blue/20 transition-all disabled:opacity-50"
            >
              <option value="">-- Seleccionar Promesa --</option>
              {promesas.map(p => <option key={p.id} value={p.id}>{p.label}</option>)}
            </select>
          </div>

          <div className="space-y-2">
            <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest">3. Vincular con Ley</label>
            <select 
              value={selectedLey}
              onChange={(e) => setSelectedLey(e.target.value)}
              className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm font-bold text-slate-700 focus:outline-none focus:ring-2 focus:ring-accent-blue/20 transition-all"
            >
              <option value="">-- Seleccionar Ley --</option>
              {leyes.map(l => <option key={l.id} value={l.id}>{l.label}</option>)}
            </select>
          </div>
        </div>

        <div className="space-y-6">
          <div className="space-y-2">
            <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest">4. Impacto Esperado</label>
            <div className="flex gap-4">
              <button 
                onClick={() => setImpacto('POSITIVO')}
                className={`flex-1 py-3 rounded-xl border text-xs font-black transition-all ${impacto === 'POSITIVO' ? 'bg-success-green text-white border-success-green shadow-md' : 'bg-white text-slate-400 border-slate-200 hover:border-success-green/50'}`}
              >
                POSITIVO (Si vota FAVOR es coherente)
              </button>
              <button 
                onClick={() => setImpacto('NEGATIVO')}
                className={`flex-1 py-3 rounded-xl border text-xs font-black transition-all ${impacto === 'NEGATIVO' ? 'bg-danger-red text-white border-danger-red shadow-md' : 'bg-white text-slate-400 border-slate-200 hover:border-danger-red/50'}`}
              >
                NEGATIVO (Si vota CONTRA es coherente)
              </button>
            </div>
          </div>

          <div className="space-y-2">
            <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest">5. Análisis de Auditoría</label>
            <textarea 
              value={analisis}
              onChange={(e) => setAnalisis(e.target.value)}
              placeholder="Explique el razonamiento técnico del vínculo..."
              className="w-full h-32 px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-accent-blue/20 transition-all"
            />
          </div>

          <button 
            onClick={handleGenerarVinculo}
            disabled={!selectedPromesa || !selectedLey || !analisis}
            className="w-full py-4 bg-primary-navy text-white rounded-xl font-black text-sm shadow-lg hover:bg-slate-800 transition-all disabled:opacity-50 flex items-center justify-center gap-2"
          >
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"/><path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"/></svg>
            GENERAR VÍNCULO DE COHERENCIA
          </button>
        </div>
      </div>
    </div>
  );
};

export default MotorCoherencia;
