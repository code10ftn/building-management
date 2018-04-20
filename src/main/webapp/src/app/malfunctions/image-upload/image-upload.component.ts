import { Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { MalfunctionService } from '../../core/http/malfunction.service';

@Component({
  selector: 'bm-image-upload',
  templateUrl: './image-upload.component.html',
  styleUrls: ['./image-upload.component.css']
})
export class ImageUploadComponent implements OnInit {
  form: FormGroup;

  loading = false;

  imageToShow: any;

  imageLoaded = false;

  @Input() buildingId: number;

  @Input() malfunctionId: number;

  @Input() enabled: boolean;

  @ViewChild('fileInput') fileInput: ElementRef;

  constructor(private fb: FormBuilder,
    private malfunctionService: MalfunctionService) {

  }

  ngOnInit(): void {
    this.createForm();
    this.getImage();
  }

  getImage() {
    this.malfunctionService.getImage(this.buildingId, this.malfunctionId).subscribe(
      res => {
        this.createImageFromBlob(res);
        this.imageLoaded = true;
      },
      err => {
        this.imageLoaded = false;
      }
    );
  }

  createImageFromBlob(image: Blob) {
    const reader = new FileReader();
    reader.addEventListener('load', () => {
      this.imageToShow = reader.result;
    }, false);

    if (image) {
      reader.readAsDataURL(image);
    }
  }

  createForm() {
    this.form = this.fb.group({
      image: [null, Validators.required]
    });
  }

  onFileChange(event) {
    if (event.target.files.length > 0) {
      const file = event.target.files[0];
      this.form.get('image').setValue(file);
    }
  }

  prepareSave(): any {
    const input = new FormData();
    input.append('image', this.form.get('image').value);
    return input;
  }

  onSubmit() {
    const formModel = this.prepareSave();
    this.loading = true;

    this.malfunctionService.upoadImage(formModel, this.buildingId, this.malfunctionId).subscribe(
      res => {
        this.loading = false;
        this.getImage();
        this.clearFile();
      },
      err => {
        this.loading = false;
        this.clearFile();
      }
    );
  }

  clearFile() {
    this.form.get('image').setValue(null);
    this.fileInput.nativeElement.value = '';
  }
}
