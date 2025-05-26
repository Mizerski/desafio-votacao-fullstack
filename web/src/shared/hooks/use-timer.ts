import { useEffect, useState, useRef } from 'react'

interface UseTimerProps {
  endTime: string | null | undefined
  onTimerEnd?: () => void
}

/**
 * Hook para gerenciar um timer regressivo
 * @param endTime - Data e hora de término
 * @param onTimerEnd - Callback executado quando o timer termina
 * @returns Objeto com informações do timer
 */
export function useTimer(endTime: string | null | undefined, onTimerEnd?: () => void) {
  const [remainingTime, setRemainingTime] = useState<number>(0)
  const [isRunning, setIsRunning] = useState<boolean>(false)
  const onTimerEndRef = useRef(onTimerEnd)
  
  // Atualiza a referência sempre que o callback muda
  useEffect(() => {
    onTimerEndRef.current = onTimerEnd
  }, [onTimerEnd])

  useEffect(() => {
    if (!endTime) {
      setRemainingTime(0)
      setIsRunning(false)
      return
    }

    const endTimestamp = new Date(endTime).getTime()
    const currentTime = new Date().getTime()
    const initialRemaining = Math.max(
      0,
      Math.floor((endTimestamp - currentTime) / 1000),
    )

    setRemainingTime(initialRemaining)
    setIsRunning(initialRemaining > 0)

    if (initialRemaining <= 0) {
      onTimerEndRef.current?.()
      return
    }

    const interval = setInterval(() => {
      setRemainingTime((prev) => {
        const newTime = prev - 1
        if (newTime <= 0) {
          clearInterval(interval)
          setIsRunning(false)
          onTimerEndRef.current?.()
          return 0
        }
        return newTime
      })
    }, 1000)

    return () => clearInterval(interval)
  }, [endTime]) // Removida dependência onTimerEnd

  const formatTime = () => {
    if (remainingTime <= 0) return '00:00'

    const minutes = Math.floor(remainingTime / 60)
    const seconds = remainingTime % 60
    return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
  }

  return {
    remainingTime,
    isRunning,
    formatTime,
    isExpired: !isRunning && endTime !== null,
  }
}
