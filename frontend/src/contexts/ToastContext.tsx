import React from 'react'

export const ToastContext = React.createContext<(message: string) => void>(
  () => undefined,
)
export const useToast = () => React.useContext(ToastContext)
