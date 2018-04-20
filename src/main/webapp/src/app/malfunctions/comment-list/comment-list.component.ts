import { Component, Input, OnInit } from '@angular/core';
import { BsModalRef, BsModalService } from 'ngx-bootstrap/modal';

import { AuthService } from '../../core/http/auth.service';
import { CommentService } from '../../core/http/comment.service';
import { ConfirmModalComponent } from '../../shared/confirm-modal/confirm-modal.component';
import { Comment } from '../../shared/model/comment.model';
import { CommentCreateDto } from '../dto/comment-create.model';

@Component({
  selector: 'bm-comment-list',
  templateUrl: './comment-list.component.html',
  styleUrls: ['./comment-list.component.css']
})
export class CommentListComponent implements OnInit {

  @Input() buildingId: number;

  @Input() malfunctionId: number;

  modalRef: BsModalRef;

  comments = new Array<Comment>();

  newComment = new CommentCreateDto();

  constructor(private authService: AuthService,
    private commentService: CommentService,
    private modalService: BsModalService) { }

  ngOnInit() {
    this.getComments();
  }

  getComments() {
    this.commentService.findAll(this.buildingId, this.malfunctionId).subscribe(
      res => {
        this.comments = res;
      },
      err => { }
    );
  }

  addComment() {
    this.commentService.create(this.newComment, this.buildingId, this.malfunctionId).subscribe(
      res => {
        this.getComments();
        this.newComment = new CommentCreateDto();
      },
      err => { }
    );
  }

  deleteComment(comment: Comment) {
    this.commentService.delete(this.buildingId, this.malfunctionId, comment.id).subscribe(
      res => {
        this.getComments();
      },
      err => { }
    );
  }

  canDelete(comment: Comment) {
    return comment.user.id === this.authService.getAuthenticatedUserId() || this.authService.isSupervisor();
  }

  openConfirmDialog(comment: Comment) {
    this.modalRef = this.modalService.show(ConfirmModalComponent);
    this.modalRef.content.onClose.subscribe(result => {
      if (result) {
        this.deleteComment(comment);
      }
    });
  }
}
