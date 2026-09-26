import { useState } from 'react'
import {
  Avatar,
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  MenuItem,
  Select,
  Stack,
  TextField,
  Typography,
} from '@mui/material'
import { useToast } from '../contexts/ToastContext'
import { useAddComment, useComments } from '../hooks/useComments'
import type { Board, Task } from '../types'

export function TaskCard({
  task,
  columns,
  onMove,
}: {
  task: Task
  columns: Board['columns']
  onMove: (task: Task, columnId: string) => Promise<void>
}) {
  const toast = useToast()
  const [open, setOpen] = useState(false)
  const [body, setBody] = useState('')
  const commentsQuery = useComments(task.id, open)
  const addCommentMutation = useAddComment(task.id)
  const comments = commentsQuery.data ?? []
  const showComments = () => setOpen(true)
  const addComment = () => {
    if (!body.trim()) return
    addCommentMutation.mutate(body, {
      onSuccess: () => setBody(''),
      onError: () => toast('Could not add comment'),
    })
  }
  return (
    <>
      <Card draggable className="task-card">
        <CardContent sx={{ p: 1.5, '&:last-child': { pb: 1.5 } }}>
          <Typography fontWeight={650}>{task.title}</Typography>
          {task.description && (
            <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>
              {task.description}
            </Typography>
          )}
          <Stack direction="row" gap={0.5} mt={1} alignItems="center">
            <Chip
              size="small"
              label={task.priority}
              color={
                task.priority === 'URGENT'
                  ? 'error'
                  : task.priority === 'HIGH'
                    ? 'warning'
                    : 'default'
              }
            />
            {task.assigneeUsername && (
              <Avatar sx={{ width: 24, height: 24, fontSize: 12 }}>
                {task.assigneeUsername[0]}
              </Avatar>
            )}
            <Button
              size="small"
              sx={{ ml: 'auto', minWidth: 0, fontSize: 11 }}
              onClick={showComments}
            >
              Comments
            </Button>
          </Stack>
          <Select
            size="small"
            fullWidth
            value={task.columnId}
            onChange={(event) => onMove(task, event.target.value)}
            sx={{ mt: 1, fontSize: 12 }}
          >
            {columns.map((column) => (
              <MenuItem key={column.id} value={column.id}>
                {column.name}
              </MenuItem>
            ))}
          </Select>
        </CardContent>
      </Card>
      <Dialog
        open={open}
        onClose={() => setOpen(false)}
        fullWidth
        maxWidth="sm"
      >
        <DialogTitle>{task.title}</DialogTitle>
        <DialogContent>
          {commentsQuery.isPending ? (
            <Typography color="text.secondary" sx={{ py: 2 }}>
              Loading comments...
            </Typography>
          ) : commentsQuery.error ? (
            <Typography color="error" sx={{ py: 2 }}>
              Could not load comments.
            </Typography>
          ) : comments.length === 0 ? (
            <Typography color="text.secondary" sx={{ py: 2 }}>
              No comments yet.
            </Typography>
          ) : (
            comments.map((comment) => (
              <Box key={comment.id} sx={{ py: 1 }}>
                <Typography variant="caption" fontWeight={700}>
                  {comment.authorName}
                </Typography>
                <Typography>{comment.body}</Typography>
              </Box>
            ))
          )}
          <TextField
            fullWidth
            multiline
            minRows={2}
            label="Add a comment"
            value={body}
            onChange={(event) => setBody(event.target.value)}
            sx={{ mt: 2 }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpen(false)}>Close</Button>
          <Button
            variant="contained"
            onClick={addComment}
            disabled={!body.trim() || addCommentMutation.isPending}
          >
            Comment
          </Button>
        </DialogActions>
      </Dialog>
    </>
  )
}
