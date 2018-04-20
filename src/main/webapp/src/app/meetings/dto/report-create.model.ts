import { TopicCommentDto } from './topic-comment.model';

export class ReportCreateDto {

    comments = new Array<TopicCommentDto>();

    comment: string;
}
