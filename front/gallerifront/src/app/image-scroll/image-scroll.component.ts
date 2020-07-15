import { Component, OnInit } from '@angular/core';
import { Image, toImageList } from '../../image';
import { HttpService } from '../http.service';

@Component({
  selector: 'app-image-scroll',
  templateUrl: './image-scroll.component.html',
  styleUrls: ['./image-scroll.component.css']
})
export class ImageScrollComponent implements OnInit {

  images: Image[] = [];

  constructor(private http: HttpService) { }

  ngOnInit() {
    this.fetchAllImageUrls();
  }

  fetchAllImageUrls(): void {
    this.http.allImageModels().subscribe({
      next: v => {
        this.images = toImageList(v);
      }
    });
  }
}
