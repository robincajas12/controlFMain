import React, { useState, useEffect, useRef } from 'react';
import PanelControlSeguridad from './COMPONENTE_PANEL_DE_CONTROL/PanelControlSeguridad';
import MotorCoherencia from './COMPONENTE_MOTOR_COHERENCIA/MotorCoherencia';
import MantenimientoSistema from './COMPONENTE_MANTENIMIENTO_DEL_SISTEMA/MantenimientoSistema';
import { useAuth } from '../../context/AuthContext';

interface AssemblyMember {
  id: number | string;
  firstName: string;
  lastname: string;
  territorial?: string;
}

interface PanelOption {
  nombreOpcion: string;
  icono: string;
  notificacionBadge?: number;
}

interface VotingItem {
  id: number;
  proposalDescription?: string;
  description?: string;
}

interface ImportResult {
  found: number;
  imported: number;
  ignored: number;
  duplicates: number;
}

interface AdminSecurityData {
  tituloSeccion: string;
  opciones: PanelOption[];
}

interface AdminMaintenanceData {
  id: string;
  titulo: string;
  codigoReferencia: string;
  estadoBaseDeDatos: boolean;
  estadoEtiqueta: string;
  fechaUltimoRespaldo: string;
  cargaServidorPorcentaje: number;
  accionesDisponibles: string[];
}

const defaultMaintenanceData: AdminMaintenanceData = {
  id: 'admin',
  titulo: 'Mantenimiento del sistema',
  codigoReferencia: 'N/D',
  estadoBaseDeDatos: false,
  estadoEtiqueta: 'OFFLINE',
  fechaUltimoRespaldo: 'Sin información',
  cargaServidorPorcentaje: 0,
  accionesDisponibles: []
};

const AdminPage: React.FC = () => {
  const [seguridad, setSeguridad] = useState<AdminSecurityData | null>(null);
  const [mantenimiento, setMantenimiento] = useState<AdminMaintenanceData | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [assemblyMembers, setAssemblyMembers] = useState<AssemblyMember[]>([]);
  const [selectedMemberId, setSelectedMemberId] = useState('');
  const [memberSearch, setMemberSearch] = useState('');
  const [showMemberDropdown, setShowMemberDropdown] = useState(false);
  const [votings, setVotings] = useState<VotingItem[]>([]);
  const [selectedVotingIds, setSelectedVotingIds] = useState<number[]>([]);
  const [isLoadingMembers, setIsLoadingMembers] = useState(true);
  const [isLoadingVotings, setIsLoadingVotings] = useState(false);
  const [isImporting, setIsImporting] = useState(false);
  const [importResult, setImportResult] = useState<ImportResult | null>(null);
  const [currentPage, setCurrentPage] = useState(1);
  const politicoDropdownRef = useRef<HTMLDivElement>(null);
  const ITEMS_PER_PAGE = 20;

  const fetchData = async () => {
    setIsLoading(true);
    try {
      const [segRes, mantRes, historicoRes] = await Promise.all([
        apiFetch('/api/admin/panel'),
        apiFetch('/api/admin/mantenimiento'),
        apiFetch('/api/admin/historico')
      ]);

      const [securityData, maintenanceData] = await Promise.all([
        segRes.ok ? (segRes.json() as Promise<AdminSecurityData>) : Promise.resolve<AdminSecurityData | null>(null),
        mantRes.ok ? (mantRes.json() as Promise<AdminMaintenanceData>) : Promise.resolve<AdminMaintenanceData | null>(null)
      ]);

      setSeguridad(securityData);
      setMantenimiento(maintenanceData);
    } catch (error) {
      console.error('Error al cargar datos administrativos:', error);
      setSeguridad(null);
      setMantenimiento(null);
    } finally {
      setIsLoading(false);
    }
  };

  const loadImportablePoliticos = async () => {
    try {
      const response = await apiFetch('/api/politicos/importables');
      const data = await response.json();
      setImportablePoliticos(data || []);
    } catch (error) {
      console.error('Error al cargar políticos importables:', error);
    }
  };

  useEffect(() => {
    if (!showPoliticoDropdown) return;

    const handleClickOutside = (event: MouseEvent) => {
      if (politicoDropdownRef.current && !politicoDropdownRef.current.contains(event.target as Node)) {
        setShowPoliticoDropdown(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [showPoliticoDropdown]);

  useEffect(() => {
    let isMounted = true;

    const loadInitialData = async () => {
      setIsLoading(true);

      try {
        const [segRes, mantRes] = await Promise.all([
          fetch('/api/admin/panel'),
          fetch('/api/admin/mantenimiento')
        ]);

        if (!isMounted) return;

        const [securityData, maintenanceData] = await Promise.all([
          segRes.ok ? (segRes.json() as Promise<AdminSecurityData>) : Promise.resolve<AdminSecurityData | null>(null),
          mantRes.ok ? (mantRes.json() as Promise<AdminMaintenanceData>) : Promise.resolve<AdminMaintenanceData | null>(null)
        ]);

        setSeguridad(securityData);
        setMantenimiento(maintenanceData);
      } catch (error) {
        console.error('Error al cargar datos administrativos:', error);
        if (isMounted) {
          setSeguridad(null);
          setMantenimiento(null);
        }
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    };

    const loadAssemblyMembers = async () => {
      try {
        setIsLoadingMembers(true);
        const response = await fetch('/admin/assembly-members');

        if (!response.ok) {
          throw new Error(`HTTP ${response.status}`);
        }

        const data = await response.json().catch(() => null);

        if (!isMounted) return;

        const normalizedMembers = Array.isArray(data)
          ? (data as AssemblyMember[])
          : [];

        setAssemblyMembers(normalizedMembers);
      } catch (error) {
        console.error('Error al cargar asambleístas:', error);
        if (isMounted) {
          setAssemblyMembers([]);
        }
      } finally {
        if (isMounted) {
          setIsLoadingMembers(false);
        }
      }
    };

    void loadInitialData();
    void loadAssemblyMembers();

    return () => {
      isMounted = false;
    };
  }, []);

  const handleAccionMantenimiento = async (accion: string) => {
    let endpoint = '';

    switch (accion) {
      case 'BACKUP': endpoint = '/mantenimiento/respaldo'; break;
      case 'CACHE_CLEAR': endpoint = '/mantenimiento/limpiar-cache'; break;
      case 'IMPORT_LEYES': endpoint = '/importar-leyes'; break;
      default: return;
    }

    try {
      await apiFetch(`/api/admin${endpoint}`, { method: 'POST' });
      alert(`Acción ${accion} ejecutada correctamente`);
      fetchData(); // Refrescar info
    } catch (error) {
      console.error(`Error al ejecutar ${accion}:`, error);
    }
  };

  const handleSeedDatabase = async () => {
    if (!confirm("¿Deseas poblar la base de datos con datos de ejemplo? Esto solo funcionará si la base de datos está vacía.")) return;

    try {
      const response = await apiFetch('/api/admin/seed', { method: 'POST' });
      if (response.ok) {
        alert("Base de datos poblada correctamente.");
        fetchData();
      } else {
        alert("Error al poblar la base de datos.");
      }
    } catch (error) {
      console.error("Error en seed:", error);
      alert("Error de conexión con el servidor.");
    }
  };

  const filteredImportablePoliticos = importablePoliticos.filter((politico) =>
    politico.label.toLowerCase().includes(politicoSearch.toLowerCase())
  );

  const filteredAssemblyMembers = assemblyMembers.filter((member) => {
    const fullName = `${member.firstName ?? ''} ${member.lastname ?? ''}`.trim();
    return fullName.toLowerCase().includes(memberSearch.toLowerCase());
  });

  const handleMemberChange = async (memberId: string) => {
    setSelectedMemberId(memberId);
    setSelectedVotingIds([]);
    setImportResult(null);
    setCurrentPage(1);

    if (!memberId) {
      setVotings([]);
      return;
    }

    try {
      setIsLoadingVotings(true);
      const response = await fetch(`/admin/assembly-members/${memberId}/votings`);

      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
      }

      const data = await response.json().catch(() => null);
      const normalizedVotings = Array.isArray(data)
        ? (data as VotingItem[])
        : [];

      setVotings(normalizedVotings);
    } catch (error) {
      console.error('Error al cargar votaciones:', error);
      setVotings([]);
    } finally {
      setIsLoadingVotings(false);
    }
  };

  const handlePoliticoSearchChange = (value: string) => {
    setPoliticoSearch(value);
    setShowPoliticoDropdown(Boolean(value.trim()));
  };

  const handlePoliticoSelect = (politico: { id: string; label: string }) => {
    setPoliticoSearch('');
    setShowPoliticoDropdown(false);
    setSelectedPoliticoIds((current) => {
      if (current.includes(politico.id)) {
        return current.filter((id) => id !== politico.id);
      }
      return [...current, politico.id];
    });
    setPoliticoImportResult(null);
  };

  const handleRemoveSelectedPolitico = (id: string) => {
    setSelectedPoliticoIds((current) => current.filter((politicoId) => politicoId !== id));
    setPoliticoImportResult(null);
  };

  const handleSyncPoliticos = async () => {
    try {
      setIsSyncingPoliticos(true);
      const response = await apiFetch('/api/admin/import-politicos', { method: 'POST' });
      const data = await response.json();
      setPoliticoSyncResult(data);
      await loadImportablePoliticos();
    } catch (error) {
      console.error('Error al sincronizar políticos:', error);
    } finally {
      setIsSyncingPoliticos(false);
    }
  };

  const handleMemberSearchChange = (value: string) => {
    setMemberSearch(value);
    setShowMemberDropdown(Boolean(value.trim()));
  };

  const handleAssemblyMemberSelect = (member: any) => {
    const label = `${member.firstName ?? ''} ${member.lastname ?? ''}`.trim();
    setMemberSearch(label);
    setShowMemberDropdown(false);
    setSelectedVotingIds([]);
    setImportResult(null);
    setCurrentPage(1);
    setSelectedMemberId(String(member.id));
    handleMemberChange(String(member.id));
  };

  const toggleVotingSelection = (votingId: number) => {
    setSelectedVotingIds((current) =>
      current.includes(votingId)
        ? current.filter((id) => id !== votingId)
        : [...current, votingId]
    );
  };

  const selectAllVotings = () => {
    setSelectedVotingIds(votings.map((voting) => voting.id));
  };

  const clearAllVotings = () => {
    setSelectedVotingIds([]);
  };

  const handleImportSelected = async () => {
    if (!selectedMemberId || selectedVotingIds.length === 0) return;

    try {
      setIsImporting(true);
      const response = await apiFetch(`/admin/import-votings/${selectedMemberId}/selected`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(selectedVotingIds)
      });
      const data = await response.json().catch(() => null);
      setImportResult((data as ImportResult | null) ?? null);
    } catch (error) {
      console.error('Error al importar votaciones:', error);
    } finally {
      setIsImporting(false);
    }
  };

  const getVoteBadgeClass = (description?: string) => {
    const normalized = (description || '').trim().toUpperCase();
    if (normalized === 'SI') return 'bg-emerald-100 text-emerald-700 border-emerald-200';
    if (normalized === 'NO') return 'bg-rose-100 text-rose-700 border-rose-200';
    if (normalized === 'ABSTENCION') return 'bg-slate-200 text-slate-700 border-slate-300';
    return 'bg-slate-100 text-slate-600 border-slate-200';
  };

  const hasSyncedPoliticos = importablePoliticos.length > 0;
  const totalPages = Math.ceil(votings.length / ITEMS_PER_PAGE);
  const paginatedVotings = votings.slice(
    (currentPage - 1) * ITEMS_PER_PAGE,
    currentPage * ITEMS_PER_PAGE
  );

  const startIndex = votings.length === 0 ? 0 : (currentPage - 1) * ITEMS_PER_PAGE + 1;
  const endIndex = Math.min(currentPage * ITEMS_PER_PAGE, votings.length);

  if (isLoading) {
    return (
      <div className="max-w-6xl mx-auto animate-pulse space-y-8">
        <div className="bg-white h-48 rounded-xl border border-slate-200"></div>
        <div className="bg-white h-96 rounded-xl border border-slate-200"></div>
        <div className="bg-white h-64 rounded-xl border border-slate-200"></div>
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto pb-12">
      <div className="mb-8 flex justify-between items-start">
        <div>
          <h2 className="text-2xl font-black text-primary-navy uppercase tracking-tighter">Panel de Control y Administración</h2>
          <p className="text-slate-500">Gestión de auditoría ciudadana, seguridad y mantenimiento de infraestructura</p>
        </div>
        <button
          onClick={handleSeedDatabase}
          className="px-4 py-2 bg-accent-blue text-white rounded-lg text-xs font-bold shadow-sm hover:bg-blue-600 transition-colors flex items-center gap-2"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" /><polyline points="7 10 12 15 17 10" /><line x1="12" y1="15" x2="12" y2="3" /></svg>
          POBLAR BASE DE DATOS (SEED)
        </button>
      </div>

      <PanelControlSeguridad
        titulo={seguridad?.tituloSeccion ?? 'Panel de control'}
        opciones={seguridad?.opciones ?? []}
      />

      <MotorCoherencia />

      <div className="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden mb-8">
        <div className="px-8 py-6 border-b border-slate-100 bg-gradient-to-r from-slate-50 via-white to-slate-50">
          <div className="flex items-center gap-3">
            <div className="rounded-2xl bg-primary-navy p-3 text-white shadow-sm">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M12 2v20" /><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7H14a3.5 3.5 0 0 1 0 7H6" /></svg>
            </div>
            <div>
              <h4 className="text-sm font-black text-primary-navy uppercase tracking-wide">Guía rápida para nuevos usuarios</h4>
              <p className="text-sm text-slate-500 mt-1">Una visión general del recorrido completo dentro del sistema.</p>
            </div>
          </div>
        </div>
        <div className="p-8 grid gap-6 lg:grid-cols-[1.1fr_0.9fr]">
          <div className="rounded-2xl border border-slate-200 bg-slate-50 p-6">
            <p className="text-sm leading-7 text-slate-700">
              El sistema está pensado para que cualquier usuario pueda empezar a entender la vida pública de manera clara y ordenada. Primero, puedes explorar el directorio de políticos y leyes para conocer a los actores, sus iniciativas y su contexto. Luego, al entrar en un perfil, puedes revisar el contenido principal, los votos, los comentarios y la coherencia de cada propuesta.
            </p>
            <p className="mt-4 text-sm leading-7 text-slate-700">
              Desde el panel administrativo, también es posible mantener los datos actualizados: importar leyes, importar votaciones y revisar el estado general del sistema. El objetivo es convertir una gran cantidad de información en una experiencia comprensible, útil y cercana para la ciudadanía.
            </p>
          </div>
          <div className="space-y-3">
            {[
              'Explora el directorio para encontrar políticos y leyes relevantes.',
              'Abre un perfil para revisar información detallada, votaciones y comentarios.',
              'Usa el panel administrativo para importar datos y mantener el sistema actualizado.',
              'Comprueba los reportes históricos para seguir la evolución de las propuestas y los resultados.'
            ].map((item, index) => (
              <div key={item} className="flex gap-3 rounded-2xl border border-slate-200 bg-white p-4 shadow-sm">
                <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-accent-blue/10 text-sm font-black text-accent-blue">
                  {index + 1}
                </div>
                <p className="text-sm leading-6 text-slate-700">{item}</p>
              </div>
            ))}
          </div>
        </div>
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden mb-8">
        <div className="px-8 py-6 border-b border-slate-100 bg-slate-50/50">
          <h4 className="text-sm font-bold text-primary-navy uppercase tracking-wide">Reporte histórico agregado</h4>
        </div>
        <div className="p-8 grid gap-4 md:grid-cols-2 xl:grid-cols-4">
          <div className="rounded-xl border border-slate-100 bg-slate-50 p-4">
            <p className="text-xs font-bold uppercase tracking-wide text-slate-500">Total de leyes</p>
            <p className="mt-2 text-3xl font-black text-primary-navy">{historico?.totalLeyes ?? 0}</p>
          </div>
          <div className="rounded-xl border border-slate-100 bg-slate-50 p-4">
            <p className="text-xs font-bold uppercase tracking-wide text-slate-500">Total de votos</p>
            <p className="mt-2 text-3xl font-black text-primary-navy">{historico?.totalVotos ?? 0}</p>
          </div>
          <div className="rounded-xl border border-slate-100 bg-slate-50 p-4">
            <p className="text-xs font-bold uppercase tracking-wide text-slate-500">Votos a favor / contra</p>
            <p className="mt-2 text-lg font-black text-primary-navy">{historico?.votosFavor ?? 0} / {historico?.votosContra ?? 0}</p>
          </div>
          <div className="rounded-xl border border-slate-100 bg-slate-50 p-4">
            <p className="text-xs font-bold uppercase tracking-wide text-slate-500">Leyes aprobadas / en debate</p>
            <p className="mt-2 text-lg font-black text-primary-navy">{historico?.leyesAprobadas ?? 0} / {historico?.leyesEnDebate ?? 0}</p>
          </div>
        </div>
      </div>

      <MantenimientoSistema
        info={mantenimiento ?? defaultMaintenanceData}
        onAccion={handleAccionMantenimiento}
      />

      

       

         
                 
<div className="mb-8">
  <div className="mb-5 flex flex-col gap-2 md:flex-row md:items-end md:justify-between">
    <div>
      <h4 className="text-sm font-black text-primary-navy uppercase tracking-wide flex items-center gap-2">
        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M12 20V10" /><path d="M18 20V4" /><path d="M6 20v-4" /></svg>
        Importaciones de datos
      </h4>
      <p className="text-sm text-slate-500 mt-1">Dos caminos independientes: elige el que se ajuste a tu objetivo. Cada uno usa endpoints reales del sistema y funciona por separado.</p>
    </div>
    <div className="inline-flex items-center gap-2 rounded-full border border-slate-200 bg-white px-3 py-1.5 text-xs font-bold text-slate-600 shadow-sm">
      <span className="h-2.5 w-2.5 rounded-full bg-accent-blue"></span>
      Dos caminos independientes
    </div>
  </div>

  {/* ===== PASO OBLIGATORIO PREVIO: Sincronizar catálogo local (necesario para AMBOS caminos) ===== */}
  <div className="rounded-2xl border-2 border-amber-300 bg-amber-50 shadow-sm overflow-hidden mb-6">
    <div className="px-6 py-4 bg-amber-100/70 border-b border-amber-200 flex items-center gap-3">
      <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-2xl bg-amber-500 text-white shadow-sm">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"><path d="M10.29 3.86 1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z" /><line x1="12" y1="9" x2="12" y2="13" /><line x1="12" y1="17" x2="12.01" y2="17" /></svg>
      </div>
      <div>
        <p className="text-[11px] font-black uppercase tracking-[0.25em] text-amber-700">Paso obligatorio · Ejecutar primero</p>
        <h3 className="text-lg font-black text-amber-900">Sincronizar catálogo local</h3>
      </div>
    </div>
    <div className="p-6">
      <p className="text-sm font-semibold text-amber-800">Este paso es imprescindible para que funcionen <span className="underline decoration-amber-400 underline-offset-2">ambos caminos</span>. Debes ejecutarlo antes que cualquier importación.</p>
      <p className="mt-2 text-sm text-amber-700/90">Carga los candidatos disponibles en la base local desde <span className="font-semibold">/api/admin/import-politicos</span>. Sin esta sincronización, ni la importación masiva ni la filtrada tendrán datos con los que trabajar.</p>
      <div className="mt-4 flex flex-col gap-3 md:flex-row md:items-center md:justify-between rounded-2xl border border-amber-200 bg-white p-4">
        <div>
          <p className="text-xs font-black uppercase tracking-[0.2em] text-amber-600">Origen de datos</p>
          <p className="mt-1 text-sm text-slate-600">Guarda los candidatos en la base local para que puedan usarse en los flujos posteriores.</p>
        </div>
        {!hasSyncedPoliticos ? (
          <button
            type="button"
            onClick={handleSyncPoliticos}
            disabled={isSyncingPoliticos}
            className="rounded-xl bg-amber-500 px-4 py-3 text-sm font-black text-white hover:bg-amber-600 transition-all shadow-sm disabled:cursor-not-allowed disabled:opacity-60"
          >
            {isSyncingPoliticos ? 'Sincronizando...' : 'Sincronizar catálogo local'}
          </button>
        ) : (
          <p className="inline-flex items-center gap-2 text-sm font-bold text-emerald-600"><span className="h-2 w-2 rounded-full bg-emerald-500"></span>Catálogo local sincronizado</p>
        )}
      </div>
      {politicoSyncResult && (
        <div className="mt-4 rounded-2xl border border-amber-200 bg-white p-4">
          <p className="text-xs font-black uppercase tracking-[0.2em] text-slate-500">Resultado de sincronización</p>
          <div className="mt-3 grid grid-cols-2 lg:grid-cols-4 gap-3">
            <div className="rounded-xl border border-slate-200 bg-slate-50 p-3"><p className="text-[10px] font-bold text-slate-500 uppercase">Encontrados</p><p className="mt-2 text-xl font-black text-primary-navy">{politicoSyncResult.found ?? 0}</p></div>
            <div className="rounded-xl border border-slate-200 bg-slate-50 p-3"><p className="text-[10px] font-bold text-slate-500 uppercase">Importados</p><p className="mt-2 text-xl font-black text-primary-navy">{politicoSyncResult.imported ?? 0}</p></div>
            <div className="rounded-xl border border-slate-200 bg-slate-50 p-3"><p className="text-[10px] font-bold text-slate-500 uppercase">Ignorados</p><p className="mt-2 text-xl font-black text-primary-navy">{politicoSyncResult.ignored ?? 0}</p></div>
            <div className="rounded-xl border border-slate-200 bg-slate-50 p-3"><p className="text-[10px] font-bold text-slate-500 uppercase">Duplicados</p><p className="mt-2 text-xl font-black text-primary-navy">{politicoSyncResult.duplicates ?? 0}</p></div>
          </div>
        </div>
      )}
    </div>
  </div>

  {/* ===== CAMINO A: Importar todas las leyes (seleccionar -> importar) ===== */}
  <div className="rounded-2xl shadow-sm border border-slate-200 bg-white overflow-hidden mb-6">
    <div className="px-6 py-5 bg-gradient-to-r from-primary-navy to-slate-800 text-white">
      <div className="flex items-center gap-3">
        <div className="rounded-2xl bg-white/10 p-2.5 text-white">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><ellipse cx="12" cy="5" rx="9" ry="3" /><path d="M3 5v14a9 3 0 0 0 18 0V5" /><path d="M3 12a9 3 0 0 0 18 0" /></svg>
        </div>
        <div>
          <p className="text-[11px] font-black uppercase tracking-[0.25em] text-white/60">Camino A · Importación masiva</p>
          <h3 className="text-lg font-black">Importar todas las leyes</h3>
        </div>
      </div>
      <p className="mt-3 text-sm text-white/80">Con el catálogo ya sincronizado, selecciona los candidatos y trae todas sus leyes en una sola operación.</p>
    </div>

    <div className="p-6 space-y-6">
      <section className="rounded-2xl border border-slate-200 bg-slate-50/70 p-5 shadow-sm">
      <div className="flex items-start gap-3">
        <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-2xl bg-accent-blue text-sm font-black text-white">
          1
        </div>
        <div className="flex-1">
          <div className="inline-flex items-center rounded-full bg-accent-blue px-3 py-1 text-[11px] font-black uppercase tracking-[0.2em] text-white">
            Selección de candidatos sincronizados
          </div>
          <h5 className="mt-3 text-lg font-black text-primary-navy">Elige los políticos ya guardados en la base local</h5>
          <p className="mt-2 text-sm text-slate-600">Esta lista se alimenta de <span className="font-semibold">/api/politicos/importables</span>, es decir, de los candidatos que ya están disponibles localmente después de la sincronización inicial.</p>
          <div className="mt-4 rounded-2xl border border-slate-200 bg-white p-4">
            <p className="text-xs font-black uppercase tracking-[0.2em] text-slate-500">Candidatos disponibles</p>
            {isLoadingMembers ? (
              <p className="mt-3 text-sm text-slate-600">Cargando candidatos sincronizados...</p>
            ) : (
              <div ref={politicoDropdownRef} className="relative mt-3">
                <input
                  type="text"
                  value={politicoSearch}
                  onChange={(e) => handlePoliticoSearchChange(e.target.value)}
                  onFocus={() => setShowPoliticoDropdown(true)}
                  placeholder="Buscar candidato sincronizado..."
                  className="w-full rounded-xl border border-slate-200 bg-white px-4 py-3 text-sm text-slate-700 shadow-sm focus:border-accent-blue focus:outline-none"
                />

                {showPoliticoDropdown && (
                  <div className="absolute z-10 w-full mt-1 bg-white border border-slate-200 rounded-xl shadow-lg max-h-64 overflow-y-auto">
                    {filteredImportablePoliticos.length > 0 ? (
                      filteredImportablePoliticos.map((politico) => {
                        const isSelected = selectedPoliticoIds.includes(politico.id);

                        return (
                          <div
                            key={politico.id}
                            onClick={() => handlePoliticoSelect(politico)}
                            className={`px-4 py-3 text-sm text-slate-700 cursor-pointer hover:bg-slate-50 border-b border-slate-100 last:border-0 ${isSelected ? 'bg-accent-blue/10 text-accent-blue font-bold' : ''}`}
                          >
                            {politico.label}
                          </div>
                        );
                      })
                    ) : (
                      <div className="px-4 py-3 text-sm text-slate-400 italic">Sin candidatos sincronizados</div>
                    )}
                  </div>
                )}
              </div>
            )}

            {selectedPoliticoIds.length > 0 && (
              <div className="mt-3 flex flex-wrap gap-2">
                {selectedPoliticoIds.map((id) => {
                  const politico = importablePoliticos.find((item) => item.id === id);
                  return (
                    <span key={id} className="inline-flex items-center gap-2 rounded-full border border-accent-blue/20 bg-accent-blue/10 px-3 py-1 text-xs font-bold text-accent-blue">
                      <span>{politico?.label ?? id}</span>
                      <button
                        type="button"
                        onClick={(event) => {
                          event.stopPropagation();
                          handleRemoveSelectedPolitico(id);
                        }}
                        className="flex h-4 w-4 items-center justify-center rounded-full bg-white text-[10px] font-black text-slate-600 hover:text-rose-600"
                        aria-label={`Eliminar ${politico?.label ?? 'candidato'}`}
                      >
                        ×
                      </button>
                    </span>
                  );
                })}
              </div>
            )}
          </div>
        </div>
      </div>
    </section>

      <section className="rounded-2xl border border-slate-200 bg-slate-50/70 p-5 shadow-sm">
        <div className="flex items-start gap-3">
          <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-2xl bg-primary-navy text-sm font-black text-white">
            2
          </div>
          <div className="flex-1">
            <div className="inline-flex items-center rounded-full bg-primary-navy px-3 py-1 text-[11px] font-black uppercase tracking-[0.2em] text-white">
              Importar leyes por candidato
            </div>
            <h5 className="mt-3 text-lg font-black text-primary-navy">Importar leyes y datos relacionados desde la base local</h5>
            <p className="mt-2 text-sm text-slate-600">Conserva el comportamiento actual del backend: toma los candidatos seleccionados y los envía a <span className="font-semibold">/admin/import-leyes</span>. En este flujo también puede persistirse información relacionada de votaciones.</p>
            <div className="mt-4 rounded-2xl border border-slate-200 bg-white p-4">
              <p className="text-xs font-black uppercase tracking-[0.2em] text-slate-500">Confirmar importación</p>
              <button
                type="button"
                onClick={handleImportLeyesPorPoliticos}
                disabled={isImportingPoliticos || selectedPoliticoIds.length === 0}
                className="mt-3 w-full rounded-xl bg-primary-navy px-4 py-3 text-sm font-black text-white hover:bg-slate-800 transition-all shadow-sm disabled:cursor-not-allowed disabled:opacity-60"
              >
                {isImportingPoliticos ? 'Importando leyes...' : 'Importar leyes por candidato'}
              </button>
            </div>
            {politicoImportResult && (
              <div className="mt-4 rounded-2xl border border-slate-200 bg-white p-4">
                <p className="text-xs font-black uppercase tracking-[0.2em] text-slate-500">Resultado</p>
                <div className="mt-4 grid grid-cols-2 lg:grid-cols-4 gap-3">
                  <div className="rounded-xl border border-slate-200 bg-slate-50 p-3"><p className="text-[10px] font-bold text-slate-500 uppercase">Solicitados</p><p className="mt-2 text-xl font-black text-primary-navy">{politicoImportResult.found}</p></div>
                  <div className="rounded-xl border border-slate-200 bg-slate-50 p-3"><p className="text-[10px] font-bold text-slate-500 uppercase">Importadas</p><p className="mt-2 text-xl font-black text-primary-navy">{politicoImportResult.imported}</p></div>
                  <div className="rounded-xl border border-slate-200 bg-slate-50 p-3"><p className="text-[10px] font-bold text-slate-500 uppercase">Ignoradas</p><p className="mt-2 text-xl font-black text-primary-navy">{politicoImportResult.ignored}</p></div>
                  <div className="rounded-xl border border-slate-200 bg-slate-50 p-3"><p className="text-[10px] font-bold text-slate-500 uppercase">Duplicadas</p><p className="mt-2 text-xl font-black text-primary-navy">{politicoImportResult.duplicates}</p></div>
                </div>
              </div>
            )}
          </div>
        </div>
      </section>
          

      </div>
    </div>
    {/* ===== CAMINO B: Importar filtrando (votaciones específicas, flujo independiente) ===== */}
    <div className="rounded-2xl shadow-sm border border-slate-200 bg-white overflow-hidden">
      <div className="px-6 py-5 bg-gradient-to-r from-accent-blue to-blue-600 text-white">
        <div className="flex items-center gap-3">
          <div className="rounded-2xl bg-white/10 p-2.5 text-white">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polygon points="22 3 2 3 10 12.46 10 19 14 21 14 12.46 22 3" /></svg>
          </div>
          <div>
            <p className="text-[11px] font-black uppercase tracking-[0.25em] text-white/60">Camino B · Importación selectiva</p>
            <h3 className="text-lg font-black">Importar filtrando</h3>
          </div>
        </div>
        <p className="mt-3 text-sm text-white/80">Elige un asambleísta y marca únicamente las votaciones que quieras importar. Es un flujo independiente del Camino A.</p>
      </div>
      <div className="p-6">
                <div className="flex-1">
                  <p className="mt-2 text-sm text-slate-600">Mantiene el flujo manual actual: consulta la API externa de asambleístas en <span className="font-semibold">/admin/assembly-members</span>, carga sus votaciones desde <span className="font-semibold">{'/admin/assembly-members/{id}/votings'}</span> y envía únicamente las seleccionadas a <span className="font-semibold">{'/admin/import-votings/{memberId}/selected'}</span>.</p>
                  <div className="mt-4 rounded-2xl border border-slate-200 bg-white p-4">
                    <p className="text-xs font-black uppercase tracking-[0.2em] text-slate-500">Buscar asambleísta</p>
                    {isLoadingMembers ? (
                      <p className="mt-3 text-sm text-slate-600">Cargando asambleístas...</p>
                    ) : (
                      <div className="relative mt-3">
                        <input
                          type="text"
                          value={memberSearch}
                          onChange={(e) => handleMemberSearchChange(e.target.value)}
                          onFocus={() => setShowMemberDropdown(true)}
                          placeholder="Buscar asambleísta..."
                          className="w-full rounded-xl border border-slate-200 bg-white px-4 py-3 text-sm text-slate-700 shadow-sm focus:border-accent-blue focus:outline-none"
                        />

                        {showMemberDropdown && (
                          <div className="absolute z-10 w-full mt-1 bg-white border border-slate-200 rounded-xl shadow-lg max-h-64 overflow-y-auto">
                            {filteredAssemblyMembers.length > 0 ? (
                              filteredAssemblyMembers.map((member) => {
                                const label = `${member.firstName ?? ''} ${member.lastname ?? ''}`.trim();
                                const isSelected = selectedMemberId === String(member.id);

                                return (
                                  <div
                                    key={member.id}
                                    onClick={() => handleAssemblyMemberSelect(member)}
                                    className={`px-4 py-3 text-sm text-slate-700 cursor-pointer hover:bg-slate-50 border-b border-slate-100 last:border-0 ${isSelected ? 'bg-accent-blue/10 text-accent-blue font-bold' : ''}`}
                                  >
                                    {label}
                                  </div>
                                );
                              })
                            ) : (
                              <div className="px-4 py-3 text-sm text-slate-400 italic">Sin resultados</div>
                            )}
                          </div>
                        )}
                      </div>
                    )}
                  </div>

                  <div className="mt-4 rounded-2xl border border-slate-200 bg-white p-4">
                    <div className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
                      <div>
                        <p className="text-xs font-black uppercase tracking-[0.2em] text-slate-500">Seleccionar votaciones</p>
                        <p className="mt-1 text-sm text-slate-600">Marca las votaciones que quieras importar para el asambleísta seleccionado.</p>
                      </div>
                      <div className="flex gap-2">
                        <button
                          type="button"
                          onClick={selectAllVotings}
                          className="bg-white border border-slate-200 rounded-xl text-xs font-black text-slate-600 hover:bg-slate-50 px-3 py-2"
                        >
                          Seleccionar todas
                        </button>
                        <button
                          type="button"
                          onClick={clearAllVotings}
                          className="bg-white border border-slate-200 rounded-xl text-xs font-black text-slate-600 hover:bg-slate-50 px-3 py-2"
                        >
                          Deseleccionar
                        </button>
                      </div>
                    </div>

                    <div className="mt-4">
                      {isLoadingVotings ? (
                        <p className="text-sm text-slate-600">Cargando votaciones...</p>
                      ) : votings.length === 0 ? (
                        <div className="rounded-xl border border-dashed border-slate-200 bg-slate-50 p-4 text-sm text-slate-600">
                          Selecciona un asambleísta para ver sus votaciones disponibles.
                        </div>
                      ) : (
                        <>
                          <div className="space-y-3">
                            {paginatedVotings.map((voting) => {
                              const isSelected = selectedVotingIds.includes(voting.id);
                              const label = (voting.proposalDescription || 'Sin descripción').length > 80
                                ? `${(voting.proposalDescription || 'Sin descripción').slice(0, 80)}...`
                                : (voting.proposalDescription || 'Sin descripción');

                              return (
                                <label
                                  key={voting.id}
                                  className="flex items-start gap-3 rounded-xl border border-slate-200 bg-slate-50 p-4 cursor-pointer hover:border-accent-blue/40 hover:bg-slate-100 transition-colors"
                                >
                                  <input
                                    type="checkbox"
                                    checked={isSelected}
                                    onChange={() => toggleVotingSelection(voting.id)}
                                    className="mt-1 h-4 w-4 rounded border-slate-300 text-accent-blue focus:ring-accent-blue"
                                  />
                                  <div className="flex-1 min-w-0">
                                    <div className="flex items-center justify-between gap-3">
                                      <p className="text-sm font-semibold text-slate-700">{label}</p>
                                      <span className={`px-2.5 py-1 rounded-full text-[10px] font-black border ${getVoteBadgeClass(voting.description)}`}>
                                        {voting.description || 'SIN DATO'}
                                      </span>
                                    </div>
                                  </div>
                                </label>
                              );
                            })}
                          </div>

                          {votings.length > 0 && (
                            <div className="mt-4 flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
                              <span className="text-xs font-bold text-slate-500 uppercase">
                                Mostrando {startIndex} - {endIndex} de {votings.length} votaciones
                              </span>
                              <div className="flex items-center gap-2">
                                <button
                                  type="button"
                                  onClick={() => setCurrentPage((page) => Math.max(1, page - 1))}
                                  disabled={currentPage === 1}
                                  className="bg-white border border-slate-200 rounded-xl text-xs font-black text-slate-600 hover:bg-slate-50 px-3 py-2 disabled:opacity-40 disabled:cursor-not-allowed"
                                >
                                  Anterior
                                </button>
                                <span className="text-xs font-bold text-slate-500 uppercase">
                                  Página {currentPage} de {totalPages}
                                </span>
                                <button
                                  type="button"
                                  onClick={() => setCurrentPage((page) => Math.min(totalPages, page + 1))}
                                  disabled={currentPage === totalPages || totalPages === 0}
                                  className="bg-white border border-slate-200 rounded-xl text-xs font-black text-slate-600 hover:bg-slate-50 px-3 py-2 disabled:opacity-40 disabled:cursor-not-allowed"
                                >
                                  Siguiente
                                </button>
                              </div>
                            </div>
                          )}
                        </>
                      )}
                    </div>
                  </div>

                  <div className="mt-4 rounded-2xl border border-slate-200 bg-white p-4">
                    <p className="text-xs font-black uppercase tracking-[0.2em] text-slate-500">Confirmar importación</p>
                    <button
                      type="button"
                      onClick={handleImportSelected}
                      disabled={isImporting || selectedVotingIds.length === 0 || !selectedMemberId}
                      className="mt-3 w-full rounded-xl bg-accent-blue px-4 py-3 text-sm font-black text-white hover:bg-blue-600 transition-all shadow-sm disabled:cursor-not-allowed disabled:opacity-60"
                    >
                      {isImporting ? 'Importando votaciones...' : 'Importar las votaciones seleccionadas'}
                    </button>
                  </div>
                  {importResult && (
                    <div className="mt-4 rounded-2xl border border-slate-200 bg-white p-4">
                      <p className="text-xs font-black uppercase tracking-[0.2em] text-slate-500">Resultado</p>
                      <div className="mt-4 grid grid-cols-2 lg:grid-cols-4 gap-3">
                        <div className="rounded-xl border border-slate-200 bg-slate-50 p-3"><p className="text-[10px] font-bold text-slate-500 uppercase">Encontradas</p><p className="mt-2 text-xl font-black text-primary-navy">{importResult.found}</p></div>
                        <div className="rounded-xl border border-slate-200 bg-slate-50 p-3"><p className="text-[10px] font-bold text-slate-500 uppercase">Importadas</p><p className="mt-2 text-xl font-black text-primary-navy">{importResult.imported}</p></div>
                        <div className="rounded-xl border border-slate-200 bg-slate-50 p-3"><p className="text-[10px] font-bold text-slate-500 uppercase">Ignoradas</p><p className="mt-2 text-xl font-black text-primary-navy">{importResult.ignored}</p></div>
                        <div className="rounded-xl border border-slate-200 bg-slate-50 p-3"><p className="text-[10px] font-bold text-slate-500 uppercase">Duplicadas</p><p className="mt-2 text-xl font-black text-primary-navy">{importResult.duplicates}</p></div>
                      </div>
                    </div>
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>
  );
};

export default AdminPage;
