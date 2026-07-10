import { Routes, Route, Navigate } from 'react-router-dom'
import MainLayout from './componentes/layout/MainLayout'
import DirectorioPoliticosPage from './componentes/directorio_politicos/DirectorioPoliticosPage'
import PerfilPoliticoPage from './componentes/perfil_politico_screen/PerfilPoliticoPage'
import DirectorioLeyesPage from './componentes/directorio_leyes/DirectorioLeyesPage'
import PerfilLeyPage from './componentes/perfil_ley/PerfilLeyPage'
import AdminPage from './componentes/panel_admin/AdminPage'
import DashboardPage from './componentes/DashboardPage'
import LoginPage from './componentes/auth/LoginPage'
import RegisterPage from './componentes/auth/RegisterPage'
import ProtectedRoute from './componentes/auth/ProtectedRoute'
import { useAuth } from './context/AuthContext'

function App() {
  const { isAuthenticated } = useAuth()

  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/registro" element={<RegisterPage />} />
      <Route path="/" element={<MainLayout />}>
        <Route
          index
          element={isAuthenticated ? <DirectorioPoliticosPage /> : <Navigate to="/login" replace />}
        />
        <Route path="dashboard" element={<DashboardPage />} />
        <Route path="politico/:id" element={<PerfilPoliticoPage />} />
        <Route path="leyes" element={<DirectorioLeyesPage />} />
        <Route path="ley/:id" element={<PerfilLeyPage />} />
        <Route path="admin" element={<ProtectedRoute adminOnly><AdminPage /></ProtectedRoute>} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Route>
    </Routes>
  )
}

export default App
