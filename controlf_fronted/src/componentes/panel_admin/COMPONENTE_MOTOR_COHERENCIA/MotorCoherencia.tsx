import React, { useState, useEffect } from 'react';
import { useAuth } from '../../../context/AuthContext';
import SearchableSelect from '../components/SearchableSelect';

/** Opción mínima (id + etiqueta) para poblar un {@link SearchableSelect}. */
interface SimpleItem {
  id: string;
  label: string;
}

/**
 * Formulario administrativo para crear vínculos de coherencia entre una
 * promesa de campaña y una ley: elige político, promesa (o crea una
 * nueva) y ley, define el impacto esperado del voto y registra el
 * análisis técnico que sustenta el vínculo.
 */
const MotorCoherencia: React.FC = () => {
  const { apiFetch } = useAuth();
  const [politicos, setPoliticos] = useState<SimpleItem[]>([]);
  const [leyes, setLeyes] = useState<SimpleItem[]>([]);
  const [promesas, setPromesas] = useState<SimpleItem[]>([]);
  
  const [selectedPolitico, setSelectedPolitico] = useState('');
  const [selectedPromesa, setSelectedPromesa] = useState('');
  const [selectedLey, setSelectedLey] = useState('');
  const [impacto, setImpacto] = useState('POSITIVO');
  const [analisis, setAnalisis] = useState('');
  const [nuevaPromesaDescripcion, setNuevaPromesaDescripcion] = useState('');
  const [nuevaPromesaCategoria, setNuevaPromesaCategoria] = useState('');
  const [nuevaPromesaFecha, setNuevaPromesaFecha] = useState('');

  // Carga las opciones de políticos y leyes disponibles para los selectores.
  useEffect(() => {
    const loadMotorData = async () => {
      try {
        const response = await apiFetch('/api/admin/motor/data');
        if (!response.ok) {
          throw new Error(`HTTP ${response.status}`);
        }
        const data = await response.json().catch(() => null);
        if (data) {
          setPoliticos(data.politicos ?? []);
          setLeyes(data.leyes ?? []);
        }
      } catch (error) {
        console.error('Error al cargar datos del motor de coherencia:', error);
      }
    };

    void loadMotorData();
  }, [apiFetch]);

  // Recarga las promesas disponibles cada vez que cambia el político seleccionado.
  useEffect(() => {
    const loadPromesas = async () => {
      if (!selectedPolitico) {
        setPromesas([]);
        return;
      }

      try {
        const response = await apiFetch(`/api/admin/politicos/${selectedPolitico}/promesas`);
        if (!response.ok) {
          throw new Error(`HTTP ${response.status}`);
        }
        const data = await response.json().catch(() => null);
        setPromesas(Array.isArray(data) ? data : []);
      } catch (error) {
        console.error('Error al cargar promesas del político:', error);
        setPromesas([]);
      }
    };

    void loadPromesas();
  }, [apiFetch, selectedPolitico]);

  /** Crea el vínculo promesa-ley con el impacto y análisis definidos en el formulario. */
  const handleGenerarVinculo = async () => {
    if (!selectedPromesa || !selectedLey) return;

    try {
      await apiFetch('/api/admin/vinculos', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          promesaId: parseInt(selectedPromesa),
          leyId: parseInt(selectedLey),
          impactoEsperado: impacto,
          nivelCoherencia: 'CUMPLE', // El nivel se fija manualmente en este formulario; no se calcula.
          analisis: analisis
        })
      });
      alert('Vínculo generado exitosamente');
      setAnalisis('');
    } catch (error) {
      console.error("Error al generar vínculo:", error);
    }
  };

  /** Registra una nueva promesa para el político seleccionado y refresca la lista de promesas. */
  const handleAgregarPromesa = async () => {
    if (!selectedPolitico || !nuevaPromesaDescripcion.trim()) return;

    try {
      const response = await apiFetch('/api/admin/promesas', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          politicoId: parseInt(selectedPolitico, 10),
          descripcion: nuevaPromesaDescripcion.trim(),
          categoria: nuevaPromesaCategoria.trim() || 'General',
          fechaPromesa: nuevaPromesaFecha ? nuevaPromesaFecha : null
        })
      });

      if (!response.ok) {
        throw new Error('No se pudo crear la promesa');
      }

      alert('Promesa creada correctamente');
      setNuevaPromesaDescripcion('');
      setNuevaPromesaCategoria('');
      setNuevaPromesaFecha('');
      const promesasResponse = await apiFetch(`/api/admin/politicos/${selectedPolitico}/promesas`);
      const promesasData = await promesasResponse.json().catch(() => null);
      setPromesas(Array.isArray(promesasData) ? promesasData : []);
    } catch (error) {
      console.error('Error al crear promesa:', error);
      alert('Error al crear la promesa');
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
            {/* Selector con búsqueda por nombre: evita listas gigantes y se cierra al hacer clic fuera. */}
            <SearchableSelect
              options={politicos}
              value={selectedPolitico}
              onChange={setSelectedPolitico}
              placeholder="-- Seleccionar Político --"
              emptyText="Sin políticos disponibles"
            />
          </div>

          <div className="space-y-2">
            <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest">2. Seleccionar Promesa</label>
            {/* Se habilita solo cuando hay un político elegido, igual que antes. */}
            <SearchableSelect
              options={promesas}
              value={selectedPromesa}
              onChange={setSelectedPromesa}
              disabled={!selectedPolitico}
              placeholder="-- Seleccionar Promesa --"
              emptyText="Sin promesas registradas"
            />
          </div>

          <div className="space-y-2">
            <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest">3. Vincular con Ley</label>
            {/* Permite buscar la ley por nombre o por su ID. */}
            <SearchableSelect
              options={leyes}
              value={selectedLey}
              onChange={setSelectedLey}
              searchById
              placeholder="-- Seleccionar Ley --"
              emptyText="Sin leyes disponibles"
            />
          </div>
        </div>

        <div className="space-y-6">
          <div className="rounded-xl border border-dashed border-slate-200 bg-slate-50 p-4 space-y-3">
            <div className="flex items-center justify-between">
              <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest">Agregar nueva promesa</label>
              <span className="text-[10px] text-slate-400">Opcional</span>
            </div>
            <textarea
              value={nuevaPromesaDescripcion}
              onChange={(e) => setNuevaPromesaDescripcion(e.target.value)}
              placeholder="Escribe la promesa del político..."
              className="w-full h-24 px-4 py-3 bg-white border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-accent-blue/20 transition-all"
            />
            <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
              <input
                value={nuevaPromesaCategoria}
                onChange={(e) => setNuevaPromesaCategoria(e.target.value)}
                placeholder="Categoría"
                className="w-full px-4 py-3 bg-white border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-accent-blue/20 transition-all"
              />
              <input
                type="date"
                value={nuevaPromesaFecha}
                onChange={(e) => setNuevaPromesaFecha(e.target.value)}
                className="w-full px-4 py-3 bg-white border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-accent-blue/20 transition-all"
              />
            </div>
            <button
              type="button"
              onClick={handleAgregarPromesa}
              disabled={!selectedPolitico || !nuevaPromesaDescripcion.trim()}
              className="w-full py-3 bg-accent-blue text-white rounded-xl font-black text-sm shadow-sm hover:bg-blue-600 transition-all disabled:opacity-50"
            >
              AGREGAR PROMESA
            </button>
          </div>

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
