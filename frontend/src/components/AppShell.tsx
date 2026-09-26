import { AppBar, Avatar, IconButton, Toolbar, Typography } from '@mui/material'
import LogoutIcon from '@mui/icons-material/Logout'
import { Navigate, Route, Routes } from 'react-router-dom'
import { Dashboard } from './Dashboard'
import { WorkspaceBoard } from './WorkspaceBoard'
import type { User } from '../types'

export function AppShell({ user }: { user: User }) {
  const signOut = () => {
    const form = document.createElement('form')
    form.method = 'POST'
    form.action = '/logout'
    document.body.appendChild(form)
    form.submit()
  }
  return (
    <>
      <AppBar position="sticky" elevation={0}>
        <Toolbar>
          <Typography variant="h6" sx={{ fontWeight: 800, flex: 1 }}>
            Agile<span className="accent">Sprint</span>
          </Typography>
          <Avatar sx={{ width: 34, height: 34, mr: 1 }}>
            {user.name?.[0] ?? 'U'}
          </Avatar>
          <Typography sx={{ mr: 2, display: { xs: 'none', sm: 'block' } }}>
            {user.name}
          </Typography>
          <IconButton color="inherit" aria-label="Sign out" onClick={signOut}>
            <LogoutIcon />
          </IconButton>
        </Toolbar>
      </AppBar>
      <Routes>
        <Route path="/" element={<Dashboard user={user} />} />
        <Route
          path="/workspaces/:workspaceId"
          element={<WorkspaceBoard user={user} />}
        />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </>
  )
}
