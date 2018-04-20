import { User } from './user/user.model';

export class Comment {

    id: number;

    content: string;

    date: Date;

    user: User;
}
