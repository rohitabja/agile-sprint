import React, { useState } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { Alert, Box, CircularProgress, Snackbar } from '@mui/material'
import './style.css'
import { AppShell } from './components/AppShell'
import { Login } from './components/Login'
import { ToastContext } from './contexts/ToastContext'
import { useAuth } from './hooks/useAuth'

const queryClient = new QueryClient()

function App() {
  const { user, loading } = useAuth()
  const [toast, setToast] = useState<string | null>(null)
  if (loading)
    return (
      <Box className="center">
        <CircularProgress />
      </Box>
    )
  if (!user) return <Login />
  return (
    <ToastContext.Provider value={setToast}>
      <AppShell user={user} />
      <Snackbar
        open={Boolean(toast)}
        autoHideDuration={4000}
        onClose={() => setToast(null)}
      >
        <Alert severity="info">{toast}</Alert>
      </Snackbar>
    </ToastContext.Provider>
  )
}

createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <App />
      </BrowserRouter>
    </QueryClientProvider>
  </React.StrictMode>,
)
