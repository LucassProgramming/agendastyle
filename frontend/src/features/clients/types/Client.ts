export interface Client {
  id: number
  firstName: string
  lastName: string
  phone: string
  email: string
  registrationDate: string
}

export type CreateClient = Pick<Client, 'firstName' | 'lastName' | 'phone' | 'email'>
