import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  Box,
  Button,
  Card,
  CardContent,
  Container,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Divider,
  Paper,
  Stack,
  TextField,
  Typography,
} from '@mui/material'
import AddIcon from '@mui/icons-material/Add'
import { api, ApiError } from '../api/client'
import { useToast } from '../contexts/ToastContext'
import type { User, Workspace } from '../types'

export function Dashboard({ user }: { user: User }) {
  const toast = useToast()
  const navigate = useNavigate()
  const [workspaces, setWorkspaces] = useState<Workspace[]>([])
  const [open, setOpen] = useState(false)
  const [form, setForm] = useState({ name: '', description: '' })

  useEffect(() => {
    api
      .workspaces()
      .then(setWorkspaces)
      .catch((error) => toast(error.message))
  }, [])

  const create = async () => {
    try {
      const workspace = await api.createWorkspace(form)
      setWorkspaces((current) => [...current, workspace])
      setOpen(false)
      setForm({ name: '', description: '' })
      navigate(`/workspaces/${workspace.id}`)
    } catch (error) {
      toast(
        error instanceof ApiError
          ? error.message
          : 'Could not create workspace',
      )
    }
  }

  return (
    <Container maxWidth="lg" sx={{ py: 5 }}>
      <Stack
        direction={{ xs: 'column', sm: 'row' }}
        justifyContent="space-between"
        alignItems={{ sm: 'center' }}
        gap={2}
        mb={4}
      >
        <Box>
          <Typography variant="h3" fontWeight={800}>
            Good to see you, {user.name.split(' ')[0]}.
          </Typography>
          <Typography color="text.secondary">
            Choose a workspace to continue planning.
          </Typography>
        </Box>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => setOpen(true)}
        >
          New workspace
        </Button>
      </Stack>
      {workspaces.length === 0 ? (
        <Paper className="empty-state">
          <Typography variant="h6">Your first sprint starts here</Typography>
          <Typography color="text.secondary" mb={2}>
            Create a workspace and invite your team.
          </Typography>
          <Button variant="outlined" onClick={() => setOpen(true)}>
            Create workspace
          </Button>
        </Paper>
      ) : (
        <Box className="workspace-grid">
          {workspaces.map((workspace) => (
            <Card
              key={workspace.id}
              className="workspace-card"
              onClick={() => navigate(`/workspaces/${workspace.id}`)}
            >
              <CardContent>
                <Typography variant="h6" fontWeight={700}>
                  {workspace.name}
                </Typography>
                <Typography color="text.secondary" sx={{ minHeight: 48 }}>
                  {workspace.description || 'No description yet'}
                </Typography>
                <Divider sx={{ my: 2 }} />
                <Typography variant="caption" color="text.secondary">
                  {workspace.memberCount} member
                  {workspace.memberCount === 1 ? '' : 's'} · Owned by{' '}
                  {workspace.ownerUsername}
                </Typography>
              </CardContent>
            </Card>
          ))}
        </Box>
      )}
      <Dialog
        open={open}
        onClose={() => setOpen(false)}
        fullWidth
        maxWidth="sm"
      >
        <DialogTitle>Create workspace</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            fullWidth
            label="Workspace name"
            margin="normal"
            value={form.name}
            onChange={(event) => setForm({ ...form, name: event.target.value })}
          />
          <TextField
            fullWidth
            multiline
            minRows={3}
            label="Description (optional)"
            margin="normal"
            value={form.description}
            onChange={(event) =>
              setForm({ ...form, description: event.target.value })
            }
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpen(false)}>Cancel</Button>
          <Button
            variant="contained"
            onClick={create}
            disabled={!form.name.trim()}
          >
            Create
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  )
}
