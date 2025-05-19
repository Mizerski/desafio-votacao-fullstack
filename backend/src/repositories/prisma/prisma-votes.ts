import { Prisma, Votes } from '@prisma/client'
import { VotesRepo } from '@repositories/votes-repo'
import { prismaClient } from '@prisma/index'

export class PrismaVotesRepo implements VotesRepo {
  async create(votes: Prisma.VotesCreateInput): Promise<Votes> {
    const vote = await prismaClient.votes.create({
      data: votes,
    })
    return vote
  }

  async findAll(): Promise<Votes[]> {
    const votes = await prismaClient.votes.findMany()
    return votes
  }

  async findByUserIdAndAgendaId(
    userId: string,
    agendaId: string,
  ): Promise<Votes | null> {
    const vote = await prismaClient.votes.findFirst({
      where: { userId, agendaId },
    })
    return vote
  }

  async count(where?: Prisma.VotesWhereInput): Promise<number> {
    const count = await prismaClient.votes.count({ where })
    return count
  }
}
