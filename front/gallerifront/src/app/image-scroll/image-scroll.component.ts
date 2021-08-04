import { Component, OnInit, ViewChild, AfterViewInit } from "@angular/core";
import { CdkVirtualScrollViewport } from "@angular/cdk/scrolling";
import { Image, ImageModelList, toImageList } from "../../image";
import { HttpService } from "../http.service";

const ITEM_HEIGHT = 300; // in pixel
const IMAGE_NOT_LOADED = "../assets/img/img_not_loaded.png";

@Component({
  selector: "app-image-scroll",
  templateUrl: "./image-scroll.component.html",
  styleUrls: ["./image-scroll.component.css"],
})
export class ImageScrollComponent implements OnInit, AfterViewInit {
  readonly LIMIT: number = 5;
  images: Image[] = [];
  displayPrevPageBtn: boolean = false;
  displayNextPageBtn: boolean = false;

  @ViewChild("virtualScroll", { static: true })
  virtualScroll: CdkVirtualScrollViewport;

  constructor(private http: HttpService) {}

  ngAfterViewInit(): void {}

  ngOnInit() {
    this.nextPage();
  }

  nextPage(): void {
    let lastId: number =
      this.images.length > 0 ? this.images[this.images.length - 1].id : null;
    this.http.nextPage(lastId, this.LIMIT).subscribe({
      next: (v: ImageModelList) => {
        if (!v) return;

        let np = toImageList(v);
        this.sortAsc(np);
        this.images = np;
        this.scrollToFirst();
      },
    });
  }

  prevPage(): void {
    let lastId: number = this.images.length > 0 ? this.images[0].id : null;
    this.http.prevPage(lastId, this.LIMIT).subscribe({
      next: (v: ImageModelList) => {
        if (!v) return;

        let vp = toImageList(v);
        this.sortAsc(vp);
        this.images = vp;
        console.log(this.images);
        this.scrollToFirst();
      },
    });
  }

  scrollToFirst(): void {
    this.virtualScroll.scrollToIndex(0, "auto");
  }

  private sortAsc(arr: Image[]): void {
    arr.sort((a, b) => {
      return a.id - b.id;
    });
  }
}
