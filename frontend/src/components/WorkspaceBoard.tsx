import { useState } from 'react'
import { useParams } from 'react-router-dom'
import {
  Box,
  Button,
  Chip,
  CircularProgress,
  Container,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Divider,
  Drawer,
  List,
  ListItemButton,
  ListItemText,
  MenuItem,
  Paper,
  Select,
  Stack,
  TextField,
  Typography,
} from '@mui/material'
import AddIcon from '@mui/icons-material/Add'
import SendIcon from '@mui/icons-material/Send'
import { ApiError, api } from '../api/client'
import { useToast } from '../contexts/ToastContext'
import { useWorkspaceBoard } from '../hooks/useWorkspaceBoard'
import { TaskCard } from './TaskCard'
import type { Task, User } from '../types'

export function WorkspaceBoard({ user }: { user: User }) {
  const { workspaceId = '' } = useParams()
  const toast = useToast()
  const state = useWorkspaceBoard(workspaceId, toast)
  const [taskOpen, setTaskOpen] = useState(false)
  const [inviteOpen, setInviteOpen] = useState(false)
  const [taskForm, setTaskForm] = useState<{
    title: string
    description: string
    priority: Task['priority']
  }>({ title: '', description: '', priority: 'MEDIUM' })
  const [invite, setInvite] = useState({ username: '', email: '' })

  const createTask = async () => {
    try {
      await state.createTask(taskForm)
      setTaskOpen(false)
      setTaskForm({ title: '', description: '', priority: 'MEDIUM' })
      toast('Task created')
    } catch (error) {
      toast(error instanceof ApiError ? error.message : 'Could not create task')
    }
  }
  const moveTask = async (task: Task, columnId: string) => {
    try {
      await state.moveTask(task, columnId)
    } catch {
      toast('Could not move task')
    }
  }
  const sendInvite = async () => {
    try {
      const member = await api.invite(workspaceId, invite)
      state.setMembers((current) => [...current, member])
      setInviteOpen(false)
      setInvite({ username: '', email: '' })
      toast('Invitation added')
    } catch (error) {
      toast(
        error instanceof ApiError ? error.message : 'Could not invite member',
      )
    }
  }
  if (!state.board)
    return (
      <Box className="center">
        <CircularProgress />
      </Box>
    )
  const isAdmin = state.members.some(
    (member) => member.username === user.username && member.role === 'ADMIN',
  )
  return (
    <Container maxWidth="xl" sx={{ py: 3 }}>
      <Stack direction={{ xs: 'column', md: 'row' }} gap={3}>
        <Drawer variant="permanent" className="workspace-drawer">
          <Box sx={{ p: 2 }}>
            <Typography variant="overline" color="text.secondary">
              Workspace
            </Typography>
            <Typography variant="h6" fontWeight={800}>
              Team board
            </Typography>
            <Divider sx={{ my: 2 }} />
            <List disablePadding>
              {state.boards.map((item) => (
                <ListItemButton
                  key={item.id}
                  selected={item.id === state.board?.id}
                  onClick={() => void state.selectBoard(item)}
                >
                  <ListItemText primary={item.name} />
                </ListItemButton>
              ))}
            </List>
          </Box>
        </Drawer>
        <Box sx={{ flex: 1, minWidth: 0 }}>
          <Stack
            direction={{ xs: 'column', sm: 'row' }}
            justifyContent="space-between"
            gap={2}
            mb={3}
          >
            <Box>
              <Typography variant="h4" fontWeight={800}>
                {state.board.name}
              </Typography>
              <Typography color="text.secondary">
                {state.members.length} collaborators · Four-stage workflow
              </Typography>
            </Box>
            <Stack direction="row" gap={1}>
              <Button
                variant="outlined"
                startIcon={<SendIcon />}
                onClick={() => setInviteOpen(true)}
                disabled={!isAdmin}
              >
                Invite
              </Button>
              <Button
                variant="contained"
                startIcon={<AddIcon />}
                onClick={() => setTaskOpen(true)}
              >
                New task
              </Button>
            </Stack>
          </Stack>
          <Box className="board-grid">
            {state.grouped.map(({ column, tasks }) => (
              <Paper key={column.id} className="column">
                <Stack direction="row" justifyContent="space-between" mb={1}>
                  <Typography fontWeight={700}>{column.name}</Typography>
                  <Chip label={tasks.length} size="small" />
                </Stack>
                {tasks.map((task) => (
                  <TaskCard
                    key={task.id}
                    task={task}
                    columns={state.columns}
                    onMove={moveTask}
                  />
                ))}
              </Paper>
            ))}
          </Box>
          <Paper sx={{ mt: 3, p: 2 }}>
            <Typography variant="h6" fontWeight={700} mb={1}>
              Activity stream
            </Typography>
            {state.activity.length === 0 ? (
              <Typography color="text.secondary">
                Task activity will appear here.
              </Typography>
            ) : (
              state.activity.slice(0, 8).map((item) => (
                <Typography key={item.id} variant="body2" sx={{ py: 0.6 }}>
                  <strong>{item.actorName}</strong> {item.message}
                </Typography>
              ))
            )}
          </Paper>
        </Box>
        <Dialog
          open={taskOpen}
          onClose={() => setTaskOpen(false)}
          fullWidth
          maxWidth="sm"
        >
          <DialogTitle>New task</DialogTitle>
          <DialogContent>
            <TextField
              autoFocus
              fullWidth
              label="Title"
              margin="normal"
              value={taskForm.title}
              onChange={(event) =>
                setTaskForm({ ...taskForm, title: event.target.value })
              }
            />
            <TextField
              fullWidth
              multiline
              minRows={3}
              label="Description"
              margin="normal"
              value={taskForm.description}
              onChange={(event) =>
                setTaskForm({ ...taskForm, description: event.target.value })
              }
            />
            <Select
              fullWidth
              value={taskForm.priority}
              onChange={(event) =>
                setTaskForm({
                  ...taskForm,
                  priority: event.target.value as Task['priority'],
                })
              }
            >
              <MenuItem value="LOW">Low</MenuItem>
              <MenuItem value="MEDIUM">Medium</MenuItem>
              <MenuItem value="HIGH">High</MenuItem>
              <MenuItem value="URGENT">Urgent</MenuItem>
            </Select>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setTaskOpen(false)}>Cancel</Button>
            <Button
              variant="contained"
              onClick={createTask}
              disabled={!taskForm.title.trim()}
            >
              Create task
            </Button>
          </DialogActions>
        </Dialog>
        <Dialog
          open={inviteOpen}
          onClose={() => setInviteOpen(false)}
          fullWidth
          maxWidth="sm"
        >
          <DialogTitle>Invite a teammate</DialogTitle>
          <DialogContent>
            <TextField
              autoFocus
              fullWidth
              label="Keycloak username"
              margin="normal"
              value={invite.username}
              onChange={(event) =>
                setInvite({ ...invite, username: event.target.value })
              }
            />
            <TextField
              fullWidth
              label="Email (optional)"
              value={invite.email}
              onChange={(event) =>
                setInvite({ ...invite, email: event.target.value })
              }
            />
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setInviteOpen(false)}>Cancel</Button>
            <Button
              variant="contained"
              onClick={sendInvite}
              disabled={!invite.username.trim()}
            >
              Invite
            </Button>
          </DialogActions>
        </Dialog>
      </Stack>
    </Container>
  )
}
