import { TopicCreateDto } from './topic-create.model';

export class MeetingCreateDto {

    date = new Date();

    topics = new Array<TopicCreateDto>();
}
