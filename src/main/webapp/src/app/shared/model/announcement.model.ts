import { User } from './user/user.model';

export class Announcement {

    id: number;

    content: string;

    timestamp: Date;

    author: User;
}
