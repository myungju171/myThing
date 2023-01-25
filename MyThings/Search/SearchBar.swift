//
//  SearchBarView.swift
//  MyThings
//
//  Created by 고명주 on 2023/01/21.
//

import SwiftUI

struct SearchBar: UIViewRepresentable {
  
  @Binding var searchLabel: String
  var onSearchButtonClicked: (() -> Void)? = nil
  
  class Coordinator: NSObject, UISearchBarDelegate {
    
    let control: SearchBar
    
    init(_ control: SearchBar) {
      self.control = control
    }
    
    func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
      control.searchLabel = searchText
    }
    
    func searchBarSearchButtonClicked(_ searchBar: UISearchBar) {
      control.onSearchButtonClicked?()
    }
  }
  
  func makeCoordinator() -> Coordinator {
    return Coordinator(self)
  }
  
  func makeUIView(context: UIViewRepresentableContext<SearchBar>) -> UISearchBar {
    let searchBar = UISearchBar(frame: .zero)
    searchBar.delegate = context.coordinator
    return searchBar
  }
  func updateUIView(_ uiView: UISearchBar, context: UIViewRepresentableContext<SearchBar>) {
    uiView.text = searchLabel
  }
  
}
