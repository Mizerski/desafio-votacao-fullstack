"use client"

import * as React from "react"
import { CalendarIcon, Clock } from "lucide-react"
import { format } from "date-fns"
import { ptBR } from "date-fns/locale"
import { Button } from "@/components/ui/button"
import { Calendar } from "@/components/ui/calendar"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { cn } from "@/components/lib/utils"

interface DateTimePickerProps {
  date: Date | undefined
  setDate: (date: Date | undefined) => void
  disabled?: boolean
}

/**
 * Componente de seleção de data e hora
 * @param date - Data selecionada
 * @param setDate - Função para definir a data
 * @param disabled - Indica se o componente está desabilitado
 */
export function DateTimePicker({ date, setDate, disabled = false }: DateTimePickerProps) {
  const [selectedTime, setSelectedTime] = React.useState<{
    hours: string
    minutes: string
  }>({
    hours: date ? format(date, "HH") : "12",
    minutes: date ? format(date, "mm") : "00",
  })

  React.useEffect(() => {
    if (date) {
      setSelectedTime({
        hours: format(date, "HH"),
        minutes: format(date, "mm"),
      })
    }
  }, [date])

  const hours = Array.from({ length: 24 }, (_, i) => i.toString().padStart(2, "0"))

  const minutes = ["00", "15", "30", "45"]

  const handleSelect = (selectedDate: Date | undefined) => {
    if (!selectedDate) {
      setDate(undefined)
      return
    }

    const hours = Number.parseInt(selectedTime.hours)
    const minutes = Number.parseInt(selectedTime.minutes)

    const newDate = new Date(selectedDate)
    newDate.setHours(hours)
    newDate.setMinutes(minutes)
    newDate.setSeconds(0)

    setDate(newDate)
  }

  const handleTimeChange = (type: "hours" | "minutes", value: string) => {
    const newTime = {
      ...selectedTime,
      [type]: value,
    }

    setSelectedTime(newTime)

    if (date) {
      const newDate = new Date(date)
      newDate.setHours(Number.parseInt(newTime.hours))
      newDate.setMinutes(Number.parseInt(newTime.minutes))
      setDate(newDate)
    }
  }

  return (
    <Popover>
      <PopoverTrigger asChild>
        <Button
          variant={"outline"}
          className={cn(
            "w-full justify-start text-left font-normal",
            !date && "text-muted-foreground",
            disabled && "opacity-50 cursor-not-allowed",
          )}
          disabled={disabled}
        >
          <CalendarIcon className="mr-2 h-4 w-4" />
          {date ? format(date, "PPP 'às' HH:mm", { locale: ptBR }) : <span>Selecione data e hora</span>}
        </Button>
      </PopoverTrigger>
      <PopoverContent className="w-auto p-0">
        <Calendar
          mode="single"
          selected={date}
          onSelect={handleSelect}
          initialFocus
          locale={ptBR}
        />
        <div className="p-3 border-t border-gray-300">
          <div className="flex items-center justify-between space-x-2">
            <div className="flex items-center space-x-2">
              <Clock className="h-4 w-4 text-gray-400" />
              <span className="text-sm text-gray-400">Hora:</span>
            </div>
            <div className="flex items-center space-x-2">
              <Select
                value={selectedTime.hours}
                onValueChange={(value) => handleTimeChange("hours", value)}
                disabled={disabled}
              >
                <SelectTrigger className="w-[70px]">
                  <SelectValue placeholder="Hora" />
                </SelectTrigger>
                <SelectContent className="max-h-[200px] overflow-y-auto">
                  {hours.map((hour) => (
                    <SelectItem key={hour} value={hour}>
                      {hour}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              <span className="text-gray-400">:</span>
              <Select
                value={selectedTime.minutes}
                onValueChange={(value) => handleTimeChange("minutes", value)}
                disabled={disabled}
              >
                <SelectTrigger >
                  <SelectValue placeholder="Min" />
                </SelectTrigger>
                <SelectContent >
                  {minutes.map((minute) => (
                    <SelectItem key={minute} value={minute}>
                      {minute}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          </div>
        </div>
      </PopoverContent>
    </Popover>
  )
}
