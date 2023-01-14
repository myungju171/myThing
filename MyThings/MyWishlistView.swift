//
//  ContentView.swift
//  MyThings
//
//  Created by 고명주 on 2023/01/15.
//

import SwiftUI
import CoreData

struct MyWishlistView: View {
  
  var body: some View {
    Text("my Wishlist")
  }
}

struct ContentView_Previews: PreviewProvider {
  static var previews: some View {
    MyWishlistView().environment(\.managedObjectContext, PersistenceController.preview.container.viewContext)
  }
}
