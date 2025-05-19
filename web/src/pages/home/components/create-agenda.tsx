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
const formSchema = z.object({
  title: z
    .string()
    .min(5, {
      message: 'O título deve ter pelo menos 5 caracteres.',
    })
    .max(100, {
      message: 'O título não pode ter mais de 100 caracteres.',
    }),
  description: z
    .string()
    .min(10, {
      message: 'A descrição deve ter pelo menos 10 caracteres.',
    })
    .max(500, {
      message: 'A descrição não pode ter mais de 500 caracteres.',
    }),
})

export function CreateAgenda() {
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false)
  const [isSuccess, setIsSuccess] = useState<boolean>(false)
  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      title: '',
      description: '',
    },
  })

  async function onSubmit() {
    try {
      setIsSubmitting(true)
      setIsSuccess(false)

      await new Promise((resolve) => setTimeout(resolve, 1000)) // Success

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
