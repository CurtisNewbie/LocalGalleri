import { Component, OnInit, Input, ViewChild, AfterViewInit } from '@angular/core';
import { Image, toImageList } from '../../image';
import { HttpService } from '../http.service';
import { CdkVirtualScrollViewport } from '@angular/cdk/scrolling';

const MIN_ITEM_HEIGHT = 300; // in pixel 

@Component({
  selector: 'app-image-scroll',
  templateUrl: './image-scroll.component.html',
  styleUrls: ['./image-scroll.component.css']
})
export class ImageScrollComponent implements OnInit {
  itemHeight: number = this.calcItemHeight();
  images: Image[] = [];

  @ViewChild("virtualScroll", { static: true })
  virtualScroll: CdkVirtualScrollViewport;

  constructor(private http: HttpService) { }

  ngOnInit() {
    console.log("Item Height:", this.itemHeight);
    this.fetchAllImageUrls();
  }

  fetchAllImageUrls(): void {
    this.http.allImageModels().subscribe({
      next: v => {
        this.images = toImageList(v);
      }
    });
  }

  private calcItemHeight(): number {
    let height = Math.floor(window.innerHeight * 0.75);
    return height < MIN_ITEM_HEIGHT ? MIN_ITEM_HEIGHT : height;
  }
}
