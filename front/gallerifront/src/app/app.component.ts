import { Component, OnInit, Input, ViewChild } from '@angular/core';
import { HttpService } from './http.service';
import { toImageList, Image } from 'src/image';

const LIMIT = 9; // 9 images per page

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html', styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'LocalGalleri';
  images: Image[] = [];
  page: number = 1;

  @ViewChild("top", { static: true })
  private topElement;

  constructor(private http: HttpService) {
  }

  ngOnInit(): void {
    this.fetghPage();
  }

  fetghPage(): void {
    this.http.imageModelsOf(this.page, LIMIT).subscribe({
      next: v => {
        if (v == null) {
          alert("This is the end.");
          this.prev();
        } else {
          this.images = toImageList(v);
        }
      }
    });
  }

  next(): void {
    if (this.images.length > 0) {
      ++this.page;
      this.fetghPage();
      this.moveToTop();
    }
  }

  prev(): void {
    if (this.page > 1) {
      --this.page;
      this.fetghPage();
      this.moveToTop();
    }
  }

  moveToTop(): void {
    this.topElement.nativeElement.scrollIntoView();
  }
}


