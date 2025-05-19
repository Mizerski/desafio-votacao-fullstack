'use client'

import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import * as z from 'zod'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import { Loader2, CheckCircle } from 'lucide-react'
import { toast, Toaster } from 'sonner'
import { useAgenda } from '@/shared/hooks/use-agenda'
import {
  AgendaResult,
  AgendaStatus,
  AgendaCategory,
} from '@/shared/types/agenda'
import { DateTimePicker } from './date-picker'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Switch } from '@/components/ui/switch'

const formSchema = z.object({
  title: z.string().min(3, 'Título inválido!').max(100),
  description: z.string().min(3, 'Descrição inválida!').max(255),
  category: z.enum([
    AgendaCategory.OUTROS,
    AgendaCategory.FINANCEIRO,
    AgendaCategory.ADMINISTRATIVO,
    AgendaCategory.ELEICOES,
    AgendaCategory.ESTATUTARIO,
    AgendaCategory.PROJETOS,
  ]),
  status: z.enum([
    AgendaStatus.OPEN,
    AgendaStatus.IN_PROGRESS,
    AgendaStatus.FINISHED,
    AgendaStatus.CANCELLED,
  ]),
  result: z.enum([
    AgendaResult.UNVOTED,
    AgendaResult.APPROVED,
    AgendaResult.REJECTED,
  ]),
  startDate: z.string().min(3, 'Data de início inválida!').max(255),
  endDate: z.string().min(3, 'Data de término inválida!').max(255),
})

export function CreateAgenda() {
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false)
  const [isSuccess, setIsSuccess] = useState<boolean>(false)
  const { createAgenda } = useAgenda()

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      title: '',
      description: '',
      category: AgendaCategory.OUTROS,
      status: AgendaStatus.OPEN,
      result: AgendaResult.UNVOTED,
      startDate: new Date().toISOString(),
      endDate: new Date(
        new Date().getTime() + 1 * 60 * 60 * 1000,
      ).toISOString(),
    },
  })

  async function onSubmit() {
    try {
      setIsSubmitting(true)
      setIsSuccess(false)

      await createAgenda({
        title: form.getValues('title'),
        description: form.getValues('description'),
        category: form.getValues('category'),
        status: form.getValues('status'),
        result: form.getValues('result'),
        startDate: form.getValues('startDate'),
        endDate: form.getValues('endDate'),
      })

      setIsSuccess(true)
      form.reset()

      toast.success('Pauta criada com sucesso!', {
        description:
          'Você já pode abrir uma sessão de votação para esta pauta.',
      })
    } catch (error) {
      console.error('Erro ao criar pauta:', error)
      toast.error('Erro ao criar pauta', {
        description:
          'Ocorreu um erro ao tentar criar a pauta. Tente novamente.',
      })
    } finally {
      setIsSubmitting(false)
      setIsSuccess(false)

      form.reset()
    }
  }

  return (
    <Card className="max-w-2xl mx-auto">
      <CardHeader>
        <CardTitle>Cadastrar Nova Pauta</CardTitle>
        <CardDescription>
          Crie uma nova pauta para votação na assembleia
        </CardDescription>
      </CardHeader>
      <CardContent>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
            <FormField
              control={form.control}
              name="title"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Título da Pauta</FormLabel>
                  <FormControl>
                    <Input
                      placeholder="Ex: Aprovação do orçamento anual"
                      {...field}
                    />
                  </FormControl>
                  <FormDescription>
                    Um título claro e objetivo para identificar a pauta.
                  </FormDescription>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="category"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Categoria</FormLabel>
                  <Select
                    onValueChange={field.onChange}
                    defaultValue={field.value}
                  >
                    <FormControl>
                      <SelectTrigger className="capitalize w-full">
                        <SelectValue placeholder="Selecione uma categoria" />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent>
                      {Object.values(AgendaCategory).map((category) => (
                        <SelectItem key={category} value={category}>
                          {category}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                  <FormDescription>
                    Selecione a categoria que melhor se adequa à pauta.
                  </FormDescription>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="description"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Descrição</FormLabel>
                  <FormControl>
                    <Textarea
                      placeholder="Descreva detalhadamente o assunto da pauta e o que será votado..."
                      className="min-h-[120px]"
                      {...field}
                    />
                  </FormControl>
                  <FormDescription>
                    Forneça informações detalhadas para que os associados possam
                    votar com conhecimento.
                  </FormDescription>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="status"
              render={({ field }) => (
                <FormItem className="flex flex-row items-center justify-between rounded-lg border border-gray-300 p-4 bg-gray-300/50">
                  <div className="space-y-0.5">
                    <FormLabel className="text-base">Agendar Votação</FormLabel>
                    <FormDescription>
                      Defina uma data e hora para início e encerramento
                      automático da votação.
                    </FormDescription>

                    {form.watch('status') !== AgendaStatus.IN_PROGRESS && (
                      <FormDescription className="text-xs text-gray-500 italic font-bold">
                        Caso a pauta não seja agendada, a votação será aberta
                        imediatamente após o cadastro. Sendo assim, a pauta será
                        encerrada após 1 hora.
                      </FormDescription>
                    )}
                  </div>
                  <FormControl>
                    <Switch
                      checked={field.value === AgendaStatus.IN_PROGRESS}
                      onCheckedChange={(checked) => {
                        field.onChange(
                          checked
                            ? AgendaStatus.IN_PROGRESS
                            : AgendaStatus.OPEN,
                        )
                      }}
                    />
                  </FormControl>
                </FormItem>
              )}
            />

            {form.watch('status') === AgendaStatus.IN_PROGRESS && (
              <div className="space-y-4">
                <FormField
                  control={form.control}
                  name="startDate"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Data de Início</FormLabel>
                      <FormControl>
                        <DateTimePicker
                          date={field.value ? new Date(field.value) : undefined}
                          setDate={(date) => {
                            field.onChange(date?.toISOString())
                          }}
                        />
                      </FormControl>
                      <FormDescription>
                        Selecione a data e hora de início da votação
                      </FormDescription>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <FormField
                  control={form.control}
                  name="endDate"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Data de Término</FormLabel>
                      <FormControl>
                        <DateTimePicker
                          date={field.value ? new Date(field.value) : undefined}
                          setDate={(date) => {
                            field.onChange(date?.toISOString())
                          }}
                        />
                      </FormControl>
                      <FormDescription>
                        Selecione a data e hora de término da votação
                      </FormDescription>
                      <FormMessage />
                    </FormItem>
                  )}
                />
              </div>
            )}

            <div className="flex justify-end gap-4">
              <Button
                type="button"
                variant="outline"
                onClick={() => form.reset()}
                disabled={isSubmitting}
              >
                Limpar
              </Button>
              <Button type="submit" disabled={isSubmitting || isSuccess}>
                {isSubmitting ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    Enviando...
                  </>
                ) : isSuccess ? (
                  <>
                    <CheckCircle className="mr-2 h-4 w-4" />
                    Pauta Criada
                  </>
                ) : (
                  'Cadastrar Pauta'
                )}
              </Button>
            </div>
          </form>
        </Form>
      </CardContent>
      <Toaster richColors />
    </Card>
  )
}
